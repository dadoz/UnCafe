package com.application.material.takeacoffee.app.facebookServices;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by davide on 27/12/14.
 */
public class FacebookLogin {
    private final Fragment currentFragment;
    public String TAG = "FacebookLogin";
    private ProfilePictureView userProfilePictureView;
    private ProgressDialog progressDialog;
    //FACEBOOK uiLifecycle
//    private UiLifecycleHelper uiHelper;

    /***** FACEBOOK LOGIN
     * @param currentFragment*****/
    public FacebookLogin(Fragment currentFragment) {
        this.currentFragment = currentFragment;
    }


    public void setUserProfilePictureView(ProfilePictureView view) {
        userProfilePictureView = view;
    }

    private void onLoginButtonClicked() {
        progressDialog = ProgressDialog.show(
                currentFragment.getActivity(), "", "Logging in...", true);
        List<String> permissions = Arrays.asList("public_profile", "user_friends", "user_about_me",
                "user_relationships", "user_birthday", "user_location");
        ParseFacebookUtils.logIn(permissions, currentFragment.getActivity(), new LogInCallback() {

            @Override
            public void done(ParseUser user, ParseException err) {
                progressDialog.dismiss();
                if (user == null) {
                    Log.d(TAG,
                            "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d(TAG,
                            "User signed up and logged in through Facebook!");
                    showUserDetailsActivity();
                } else {
                    Log.d(TAG,
                            "User logged in through Facebook!");
                    showUserDetailsActivity();
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

    private void showUserDetailsActivity() {

    }

    private void makeMeRequest() {
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
                                if (user.getLocation().getProperty("name") != null) {
                                    userProfile.put("location", (String) user
                                            .getLocation().getProperty("name"));
                                }
                                if (user.getProperty("gender") != null) {
                                    userProfile.put("gender",
                                            (String) user.getProperty("gender"));
                                }
                                if (user.getBirthday() != null) {
                                    userProfile.put("birthday",
                                            user.getBirthday());
                                }
                                if (user.getProperty("relationship_status") != null) {
                                    userProfile
                                            .put("relationship_status",
                                                    (String) user
                                                            .getProperty("relationship_status"));
                                }
                                // Now add the data to the UI elements
                                // ...

                            } catch (JSONException e) {
                                Log.d(TAG,
                                        "Error parsing returned user data.");
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
                                Log.d(TAG,
                                        "Some other error: "
                                                + response.getError()
                                                .getErrorMessage());
                            }

                        }
                    }

                });
        request.executeAsync();
    }

    private void updateViewsWithProfileInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.get("profile") != null) {
            JSONObject userProfile = currentUser.getJSONObject("profile");
            try {
                if (userProfile.getString("facebookId") != null) {
                    String facebookId = userProfile.get("facebookId")
                            .toString();
                    userProfilePictureView.setProfileId(facebookId);
                } else {
                    // Show the default, blank user profile picture
                    userProfilePictureView.setProfileId(null);
                }
                // Set additional UI elements
                // ...
            } catch (JSONException e) {
                // handle error
            }

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
