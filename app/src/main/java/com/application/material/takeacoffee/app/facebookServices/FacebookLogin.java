package com.application.material.takeacoffee.app.facebookServices;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by davide on 27/12/14.
 */
public class FacebookLogin {
    private final Fragment currentFragment;
    //FACEBOOK uiLifecycle
//    private UiLifecycleHelper uiHelper;

    /***** FACEBOOK LOGIN
     * @param currentFragment*****/

    public FacebookLogin(Fragment currentFragment) {
        this.currentFragment = currentFragment;
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
