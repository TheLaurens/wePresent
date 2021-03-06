package wepresent.wepresent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import wepresent.wepresent.mappers.AsyncTaskReport;
import wepresent.wepresent.mappers.MainMapper;
import wepresent.wepresent.mappers.Mapper;
import wepresent.wepresent.mappers.RegisterMapper;

public class RegisterActivity extends Activity implements AsyncTaskReport {

    private String errorMessage, androidId;
    SharedPreferences sharedpreferences;

    private RegisterMapper registerMapper;
    private MainMapper loginMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedpreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        loginMapper = new MainMapper(this);

        // Get the unique device ID
        Bundle intentInfo = getIntent().getExtras();

        if(intentInfo.getString( "AndroidID" ) != null) {
            androidId = intentInfo.getString( "AndroidID" );
        } else {
            // TODO: Prevent from registering without unique ID
            //dat ga ik nou niet doen he
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Moves message to other text unit
     */
    public void register(View view){
        TextView username = (TextView) findViewById(R.id.title);
        TextView email = (TextView) findViewById(R.id.email);
        TextView password = (TextView) findViewById(R.id.passWord);
        TextView verification = (TextView) findViewById(R.id.verPass);
        String yourUsername = username.getText().toString();
        String yourEmail = email.getText().toString();
        String yourPassword = password.getText().toString();
        String verifyPassword = verification.getText().toString();

        if (yourUsername.equals("") || yourEmail.equals("") || yourPassword.equals("") || verifyPassword.equals("")){
            showErrorMessage("Please fill in all fields");
        }
        else if(!yourPassword.equals(verifyPassword)){
            showErrorMessage("Your passwords do not match");
        } else {
            registerMapper = new RegisterMapper(this);
            registerMapper.start(yourUsername, yourPassword, yourEmail, androidId);
        }
    }

    public void showErrorMessage(String errorMessage) {
        TextView message = (TextView) findViewById(R.id.errorMessage);
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    private void proceedLogin(String userName, String password, String deviceID){
        loginMapper = new MainMapper(this);
        loginMapper.start(userName, password, deviceID);
    }

    public void done(Mapper.MapperSort mapper) {
        if (mapper.equals(loginMapper.getMapperSort())){
            if (loginMapper.isLoginsuccesful()) {
                int UserID = loginMapper.getUserID();

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt("UserID", UserID);
                editor.putString("AndroidID", loginMapper.getAndroidId());
                editor.putBoolean("LoggedIn", true);
                editor.putInt("SessionID", 0);
                editor.commit();
                //Intent out = new Intent(this, SessionActivity.class);
                //out.putExtra("LoggedIn", true);
                //out.putExtra("UserID", loginMapper.getUsername());
                Intent out = new Intent(this, LauncherHubThing.class);
                out.putExtra("Tab", "slides");
                startActivity(out);
            } else {
                Toast.makeText(getApplicationContext(), "Cannot login", Toast.LENGTH_LONG).show();
                int registeredID = registerMapper.getUserId();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("UserID", registeredID);
                startActivity(intent);
            }
        } else {
            if (registerMapper.isRegisterSuccesful()) {
                // TODO: Redirect to hub
                //            int registeredID = registerMapper.getUserId();
                //            Intent intent = new Intent(this, MainActivity.class);
                //            intent.putExtra("UserID", registeredID);
                //            startActivity(intent);
                proceedLogin(registerMapper.getUsername(), registerMapper.getPassword(), registerMapper.getAndroidId());
            } else {
                // Check what error message occurs
                switch (registerMapper.getErrorCode()) {
                    case 1:
                        this.errorMessage = "This username is already in use";
                        break;
                    case 2:
                        this.errorMessage = "This emailaddress is already in use";
                        break;
                    case 3:
                        this.errorMessage = "There was an internal error, please try again later";
                        break;
                    default:
                        this.errorMessage = "Unknown error";
                        break;
                }

                // Show the error message
                showErrorMessage(this.errorMessage);
            }
        }
    }
}
