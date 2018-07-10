package me.rlxu.parsetagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginBtn;
    private Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // implement persistence login
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_main);
            getSupportActionBar().hide();

            usernameInput = findViewById(R.id.username_et);
            passwordInput = findViewById(R.id.password_et);
            loginBtn = findViewById(R.id.login_btn);
            signupBtn = findViewById(R.id.btnSignUp);

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String username = usernameInput.getText().toString();
                    final String password = passwordInput.getText().toString();

                    login(username, password);
                }
            });

            signupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "Login successful!");
                    final Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("LoginActivity", "Login failed");
                    e.printStackTrace();
                }
            }
        });
    }
}
