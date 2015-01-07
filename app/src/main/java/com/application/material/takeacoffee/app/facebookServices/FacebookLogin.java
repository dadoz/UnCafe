package com.application.material.takeacoffee.app.facebookServices;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.application.DataApplication;
import com.application.material.takeacoffee.app.models.User;
import com.application.material.takeacoffee.app.singletons.ImagePickerSingleton;
import com.application.material.takeacoffee.app.singletons.PrivateApplicationDirSingleton;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.google.gson.JsonObject;
import com.parse.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by davide on 27/12/14.
 */
public class FacebookLogin {
    private final Activity activityRef;
    private final DataApplication dataApplication;
    public String TAG = "FacebookLogin";
    private static ImageView userProfilePictureView;
    private ProgressDialog progressDialog;
    private static FacebookLogin facebookLoginInstance;
    public static final int REQUEST_CODE_FB = 1002;
    private static EditText usernameTextView;

    //FACEBOOK uiLifecycle
//    private UiLifecycleHelper uiHelper;

    /***** FACEBOOK LOGIN
     * @param activityRef*****/
    private FacebookLogin(Activity activityRef) {
        //init parse
        Parse.initialize(activityRef,
                activityRef.getResources()
                        .getString(R.string.parseApplicationId),
                activityRef.getResources()
                        .getString(R.string.parseClientKey));

        //init ParceFacebookUtils
        ParseFacebookUtils.initialize(activityRef.getResources()
                .getString(R.string.facebook_app_id));

        dataApplication = (DataApplication) activityRef.getApplication();
        this.activityRef = activityRef;
    }

    public static FacebookLogin getInstance(Activity activityRef) {
        return facebookLoginInstance == null ?
                facebookLoginInstance = new FacebookLogin(activityRef) :
                facebookLoginInstance;
    }

    public void setUserProfilePictureView(ImageView view) {
        userProfilePictureView = view;
    }

    public void setUsernameTextView(EditText view) {
        usernameTextView = view;
    }

    public void onLoginButtonClicked() {
        progressDialog = ProgressDialog.show(
                activityRef, "", "Logging in...", true);
        List<String> permissions = Arrays.asList("public_profile");
//        List<String> permissions = Arrays.asList("public_profile", "user_friends", "user_about_me",
//                "user_relationships", "user_birthday", "user_location");
        ParseFacebookUtils.logIn(permissions, activityRef, REQUEST_CODE_FB, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                progressDialog.dismiss();
                if (user == null) {
                    Log.d(TAG,
                            "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d(TAG,
                            "User signed up and logged in through Facebook!");
                    showUserDetailsActivity(user);
                } else {
                    Log.d(TAG,
                            "User logged in through Facebook!");
                    showUserDetailsActivity(user);
                }
            }
        });
    }

    private void onLogoutButtonClicked() {
        // Log the user out
        ParseUser.logOut();

        // Go to the login view
//        startLoginActivity();
    }

    private void showUserDetailsActivity(ParseUser user) {
        makeMeRequest();
    }

    public void makeMeRequest() {
        Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            //TODO success - move out
                            // Create a JSON object to hold the profile info
                            JSONObject userProfile = new JSONObject();
                            try {
                                // Populate the JSON object
                                userProfile.put("facebookId", user.getId());
                                userProfile.put("name", user.getName());
//                                if (user.getLocation().getProperty("name") != null) {
//                                    userProfile.put("location", (String) user
//                                            .getLocation().getProperty("name"));
//                                }
//                                if (user.getProperty("gender") != null) {
//                                    userProfile.put("gender",
//                                            (String) user.getProperty("gender"));
//                                }
//                                if (user.getBirthday() != null) {
//                                    userProfile.put("birthday",
//                                            user.getBirthday());
//                                }
//                                if (user.getProperty("relationship_status") != null) {
//                                    userProfile
//                                            .put("relationship_status",
//                                                    (String) user
//                                                            .getProperty("relationship_status"));
//                                }

                                //TODO move out
                                // Now add the data to the UI elements
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                currentUser.put("profile", userProfile);

                                //set username
                                updateViewsWithUsername(); //ASYNC
                                //set profile pic
                                updateViewsWithProfileInfoAsyncTask(); //ASYNC
//                                updateViewsWithProfileInfo(); //ASYNC

                            } catch (JSONException e) {
                                Log.d(TAG, "Error parsing returned user data.");
                            } catch (Exception e) {
                                Log.d(TAG, "Error data." + e.getMessage());
                            }

                        } else if (response.getError() != null) {
                            //TODO error handling - move out
                            if ((response.getError().getCategory() ==
                                    FacebookRequestError.Category.AUTHENTICATION_RETRY) ||
                                    (response.getError().getCategory() ==
                                            FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
                                Log.d(TAG,
                                        "The facebook session was invalidated.");
                                onLogoutButtonClicked();
                            } else {
                                Log.d(TAG, "Some other error: " +
                                        response.getError().getErrorMessage());
                            }

                        }
                    }

                });
        request.executeAsync();
    }
/*
    private void storeUser() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.get("profile") != null) {
            JSONObject userProfile = currentUser.getJSONObject("profile");

            try {
                String facebookId = null;
                if (userProfile.getString("facebookId") != null) {
                    facebookId = userProfile.get("facebookId")
                            .toString();
                }
                String username = userProfile.getString("name");

//                User loggedUser = new User(null, facebookId, username);
//                dataApplication.setUser(loggedUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
*/
    private void updateViewsWithUsername() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.get("profile") != null) {
            JSONObject userProfile = currentUser.getJSONObject("profile");

            try {
                Log.e(TAG, "hey your username: " + userProfile.get("name"));
                //set username
                usernameTextView.setText(userProfile.getString("name"));
                //set profilePIcture
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
/*
    private void updateViewsWithProfileInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.get("profile") != null) {
            JSONObject userProfile = currentUser.getJSONObject("profile");
            try {
                if (userProfile.getString("facebookId") != null) {
                    String facebookId = userProfile.get("facebookId")
                            .toString();
                    userProfilePictureView.setProfileId(facebookId);
                    return;
                }

                // Show the default, blank user profile picture
                userProfilePictureView.setProfileId(null);
                // Set additional UI elements
                // ...
            } catch (JSONException e) {
                // handle error
                e.printStackTrace();
            } finally {
                onLogoutButtonClicked();
            }

        }
    }
*/
    public void updateViewsWithProfileInfoAsyncTask() {
        //profile pic
        Bundle params = new Bundle();
        params.putBoolean("redirect", false);
        params.putString("type", "normal");
        params.putString("height", "300");
        params.putString("width", "300");
//        params.putString("height", String.valueOf(activityRef
//                .getResources().getDimension(R.dimen.huge_icon_size)));
//        params.putString("width", String.valueOf(activityRef
//                .getResources().getDimension(R.dimen.huge_icon_size)));
        new Request(ParseFacebookUtils.getSession(),
                "/me/picture",
                params,
                HttpMethod.GET,
                new Request.Callback() {

                    @Override
                    public void onCompleted(Response response) {
                        Log.e(TAG, "response" + response);

                        if(response == null ||
                                response.getGraphObject() == null) {
                            Log.e(TAG, "error - response null from FB stateChange"); //IMPROVEMENT check if there is internet connection
                            ParseFacebookUtils.getSession().close();
//                            progressDialog.cancel();
                            return;
                        }

                        try {
                            JSONObject dataJSON = (JSONObject) response.getGraphObject().getProperty("data");
                            Log.e(TAG, "url of my profile pic -" + dataJSON.getString("url"));
                            URL profilePicURL = new URL(dataJSON.getString("url"));

                            Bitmap profilePicture = new ProfilePictureAsyncTask()
                                    .execute(profilePicURL).get();

                            ImagePickerSingleton imagePickerSingleton = ImagePickerSingleton.getInstance(activityRef);
                            profilePicture = imagePickerSingleton.getRoundedPicture(profilePicture);

                            String profilePictureUrl = BitmapStore.store(profilePicture,
                                    PrivateApplicationDirSingleton.getDir(activityRef));
                            userProfilePictureView.setImageBitmap(profilePicture);
                            userProfilePictureView.setTag(profilePictureUrl);
                            profilePicture = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            //please count ref to release
                            ParseFacebookUtils.getSession().close();
//                            progressDialog.cancel();
                        }
                    }
                }
        ).executeAsync();


    }

    private class ProfilePictureAsyncTask extends AsyncTask<URL, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(URL... urls) {
            Bitmap profilePicture;
            try {
                profilePicture = BitmapFactory.decodeStream(urls[0].openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return profilePicture;
        }
    }


    public static class BitmapStore {
        private static String PROFILE_PIC_FILE_NAME = "profilePicture.png";

        public static String store(Bitmap profileImage, File customDir) {
            FileOutputStream out = null;
            if(profileImage == null) {
                return null;
            }

            File profilePicFile = new File(customDir, PROFILE_PIC_FILE_NAME); //Getting a file within the dir.
            try {
                out = new FileOutputStream(profilePicFile);
                profileImage.compress(Bitmap.CompressFormat.PNG, 90, out);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if(out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return profilePicFile.getAbsolutePath();
        }

    }



/*    public void initFacebookInstance() {
        uiHelper.onCreate(savedInstance);
        Session session = Session.getActiveSession();
        if (session!= null && !session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(currentFragment)
                    .setCallback(callback));
        } else {
            Session.openActiveSession(getActivity(), currentFragment, true, callback);
        }

    }


    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Signing in...");

        Log.e(TAG, "status fb closed: " + state.isClosed() + " - open: " + state.isOpened());
        if (state.isOpened()) {
            Log.i(TAG, "Logged in... Facebook");
            pd.show();

            //get fb userId
            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {

                    if(response == null || response.getGraphObject() == null ||
                            session != Session.getActiveSession() || user == null) {
                        Log.e(TAG, "error - response null from FB stateChange"); //IMPROVEMENT check if there is internet connection
                        Common.displayError(getActivity().getApplicationContext(), "failed to get data from facebook");
                        session.close();
                        pd.cancel();
                        return;
                    }

                    String userId = user.getId();
                    String username = user.getFirstName();

                    ((EditText) mainActivityRef.findViewById(R.id.usernameNewUserEditTextId)).setText(username);
                    Log.e(TAG, "userId" + userId + " -- " + username);

                    //profile pic
                    Bundle params = new Bundle();
                    params.putBoolean("redirect", false);
                    params.putString("height", "300");
                    params.putString("type", "normal");
                    params.putString("width", "300");
                    new Request(session,
                            "/me/picture",
                            params,
                            HttpMethod.GET,
                            new Request.Callback() {

                                @Override
                                public void onCompleted(Response response) {
                                    Log.e(TAG, "response" + response);

                                    if(response == null || response.getGraphObject() == null ||
                                            session != Session.getActiveSession()) {
                                        Log.e(TAG, "error - response null from FB stateChange"); //IMPROVEMENT check if there is internet connection
                                        Common.displayError(getActivity().getApplicationContext(), "failed to get data from facebook");
                                        session.close();
                                        pd.cancel();
                                        return;
                                    }

                                    JSONObject dataJSON = (JSONObject) response.getGraphObject().getProperty("data");

                                    try {

                                        Log.e(TAG, "url of my profile pic -" + dataJSON.getString("url"));
                                        URL profilePicURL = new URL(dataJSON.getString("url"));
                                        Bitmap profilePicture = new ProfilePictureAsyncTask().execute(profilePicURL).get();
                                        ImageView profilePicView = (ImageView) mainActivityRef.findViewById(R.id.profilePicImageViewId);
                                        //TODO REFACTOR IT
                                        //setProfilePicture(profilePicture, profilePicView);
                                        //LOGOUT FROM FB/

                                        session.close();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                    session.close();
                                    pd.cancel();
                                }
                            }
                    ).executeAsync();
                }
            });
            Request.executeBatchAsync(request);

        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    private class ProfilePictureAsyncTask extends AsyncTask<URL, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(URL... urls) {
            Bitmap profilePicture;
            try {
                profilePicture = BitmapFactory.decodeStream(urls[0].openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return profilePicture;
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

*/
}
