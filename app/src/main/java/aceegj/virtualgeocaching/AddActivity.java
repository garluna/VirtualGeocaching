package aceegj.virtualgeocaching;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class AddActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    private static int PICK_IMAGE_REQUEST = 42;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private AddToGeocacheTask mAddToGeocacheTask = null;

    // UI references.
    private EditText mNameView;
    private EditText mMessageView;
    private Button mAddImageButton;
    private View mProgressView;
    private View mMessageFormView;

    private OnClickListener addOnClickListener;
    private OnClickListener removeOnClickListener;

    private Uri mUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                mAddImageButton.setText(R.string.action_remove_image);
                mAddImageButton.setOnClickListener(removeOnClickListener);
                mUri = data.getData();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        setupActionBar();
        mNameView = (EditText) findViewById(R.id.name);
        mMessageView = (EditText) findViewById(R.id.message);
        mAddImageButton = (Button) findViewById(R.id.add_image);

        addOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(intent, "Add Image"), PICK_IMAGE_REQUEST);
            }
        };
        removeOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                mAddImageButton.setText(R.string.action_add_image);
                mAddImageButton.setOnClickListener(addOnClickListener);
                mUri = null;
            }
        };

        // Set up the login form.
        mAddImageButton.setOnClickListener(addOnClickListener);

        Button mDoneButton = (Button) findViewById(R.id.done_button);
        mDoneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSendMessage();
            }
        });

        mMessageFormView = findViewById(R.id.message_form);
        mProgressView = findViewById(R.id.message_progress);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int code, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //if (requestCode == REQUEST_READ_CONTACTS) {
        //    if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        //        populateAutoComplete();
        //    }
        //}
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSendMessage() {
        if (mAddToGeocacheTask != null) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mMessageView.setError(null);

        // Store values at the time of the message attempt.
        String name = mNameView.getText().toString();
        String message = mMessageView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        /*if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt message and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user message attempt.
            showProgress(true);
            mAddToGeocacheTask = new AddToGeocacheTask(name, message, mUri);
            mAddToGeocacheTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the message form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mMessageFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mMessageFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mMessageFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mMessageFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class AddToGeocacheTask extends AsyncTask<Void, Void, Boolean> {
        private final String mName;
        private final String mMessage;
        private final Uri mImageUri;

        AddToGeocacheTask(String name, String message, Uri imageUri) {
            mName = name;
            mMessage = message;
            mImageUri = imageUri;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
                /*String requestString = "";
                DataInputStream dis = null;
                StringBuffer messagebuffer = new StringBuffer();
                URL url = new URL("http", "localhost", "recieve");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");

                String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

                InputStream input = AddActivity.this.getContentResolver().openInputStream(uri);
                BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
                onlyBoundsOptions.inJustDecodeBounds = true;
                onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
                BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
                input.close();

                if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
                    return null;
                }

                int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

                double THUMBNAIL_SIZE = 128f;
                double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
                bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
                input = AddActivity.this.getContentResolver().openInputStream(mImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
                input.close();
                bitmap.compress(new Bitmap.CompressFormat());

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", mName);
                jsonObject.put("message", mMessage);
                jsonObject.put("image", mImageUri); //CHANGE THIS TODO ELTON
                jsonObject.put("time", date);

                OutputStream out = new BufferedOutputStream(httpURLConnection.getOutputStream());

                out.write(requestString.getBytes());

                out.flush();

                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());

                dis = new DataInputStream(in);

                int ch;

                long len = httpURLConnection.getContentLength();

                if (len != -1) {

                    for (int i = 0; i < len; i++)

                        if ((ch = dis.read()) != -1) {

                            messagebuffer.append((char) ch);
                        }
                } else {

                    while ((ch = dis.read()) != -1)
                        messagebuffer.append((char) ch);
                }

                dis.close();*/


            } catch (Exception e) {
                return false;
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAddToGeocacheTask = null;
            showProgress(false);

            if (success) {
                String date = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());
                GeocacheData.getGeocacheData().messagesMap.get(getIntent().getExtras().get("LatLng")).add(new GeocacheData.GeocacheMessage(mName, date, mMessage, mUri));
                finish();
            } else {
                // fail
            }
        }

        @Override
        protected void onCancelled() {
            mAddToGeocacheTask = null;
            showProgress(false);
        }
    }
}

