package aceegj.virtualgeocaching;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            mAddImageButton.setText(R.string.action_remove_image);
            mAddImageButton.setOnClickListener(removeOnClickListener);
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
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Add Image"), PICK_IMAGE_REQUEST);
            }
        };
        removeOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                mAddImageButton.setText(R.string.action_add_image);
                mAddImageButton.setOnClickListener(addOnClickListener);
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
            mAddToGeocacheTask = new AddToGeocacheTask(name, message);
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

        AddToGeocacheTask(String name, String message) {
            mName = name;
            mMessage = message;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
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

