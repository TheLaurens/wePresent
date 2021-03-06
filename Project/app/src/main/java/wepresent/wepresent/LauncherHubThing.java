package wepresent.wepresent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;
import wepresent.wepresent.mappers.AsyncTaskReport;
import wepresent.wepresent.mappers.LeaveSessionMapper;
import wepresent.wepresent.mappers.Mapper;

public class LauncherHubThing extends MaterialNavigationDrawer implements MaterialSectionListener, AsyncTaskReport {

    HomeFragment homeFragment;
    boolean loggedIn;
    int userID;
    int sessionID;
    private LeaveSessionMapper leaveSessionMapper;
    SharedPreferences sharedpreferences;
    boolean owner;
    String sessName;

    @Override
    public void init(Bundle savedInstanceState) {
        // Create home fragment
        Intent in = getIntent();
        String tab = in.getStringExtra("Tab");
        loggedIn = in.getBooleanExtra("LoggedIn",false);
        sharedpreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);

        boolean leaved = in.getBooleanExtra("Leaved", false);
        userID = sharedpreferences.getInt("UserID", 0);
        if(leaved){
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("LoggedIn", true);
            editor.putInt("SessionID", 0);
            editor.commit();

            leaveSessionMapper = new LeaveSessionMapper(this);
            leaveSessionMapper.start(userID);
        }

        Bundle sessBundle = new Bundle();

        sessionID = sharedpreferences.getInt("SessionID", 0);
        loggedIn = sharedpreferences.getBoolean("LoggedIn", false);
        owner = sharedpreferences.getBoolean("Owner", false);
        if (sessionID>0) {
            sessName = sharedpreferences.getString("SessionName", "Session");
        } else {
            sessName = "Not in a session";
        }

        // Determine for what tab it is
        switch (tab) {
            case "slides":
                sessBundle.putInt("SessionID", sessionID);
                sessBundle.putString("Tab", "slides");
                break;
            case "quiz":
                String question = in.getStringExtra("Question");
                String type = in.getStringExtra("Type");
                // Determine if multiple choice
                if( type.equals("multiplechoice") ) {
                    String button1 = in.getStringExtra("Button1");
                    String button2 = in.getStringExtra("Button2");
                    String button3 = in.getStringExtra("Button3");
                    sessBundle.putString("Button1", button1);
                    sessBundle.putString("Button2", button2);
                    sessBundle.putString("Button3", button3);
                }

                // Add it to the bundle
                sessBundle.putString("Question", question);
                sessBundle.putInt("QuestionID", in.getIntExtra("QuestionID", 0));
                sessBundle.putString("Type", type);
                sessBundle.putString("Tab", tab);

                break;
        }
        sessionID = sharedpreferences.getInt("SessionID", 0);
        sessBundle.putInt("SessionID", sessionID);
        //userID = sharedpreferences.getInt("UserID", 0);
        sessBundle.putInt("UserID", userID);
        System.out.println("Dit is nu de userID: " + userID);

        homeFragment = new HomeFragment();
        homeFragment.setArguments(sessBundle);
        setupNavigationDrawer();
        setDrawerHeaderImage(R.drawable.menuthing);
        if (sessionID==0) {
            this.openDrawer();
        }
    }

    /**
     * Sets up the items for the navigation drawer
     */
    private void setupNavigationDrawer() {
        // Options
        this.disableLearningPattern();
        this.allowArrowAnimation();
        // Back pattern
        this.setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_TO_FIRST);


        // The sections
        MaterialSection section = newSection(
            sessName,
            homeFragment
        );
        this.addSection(section);
        Intent in;

        System.out.println("sessionID: " + sessionID);
        System.out.println("loggedIn: " + loggedIn);

        if(sessionID!=0) {
            in = new Intent(this, SessionActivity.class);
            in.putExtra("LoggedIn", true);
            in.putExtra("UserID", userID);
            section = newSection(
                    "Switch Session",
                    in
            );
            this.addSection(section);

            in = new Intent(this, LauncherHubThing.class);
            in.putExtra("Tab", "slides");
            in.putExtra("Leaved", true);
            section = newSection(
                    "Leave Session",
                    in
            );
            this.addSection(section);

        } else {
            in = new Intent(this, SessionActivity.class);
            in.putExtra("LoggedIn", true);
            in.putExtra("UserID", userID);
            section = newSection(
                    "Join Session",
                    in
            );
            this.addSection(section);
        }
        if (loggedIn){
            in = new Intent(this, MainActivity.class);
            in.putExtra("LoggedOut", true);
            in.putExtra("Leaved", true);
            in.putExtra("OlderUserID", userID);
            section = newSection(
                    "Logout",
                    in
            );
            this.addBottomSection(section);

            in = new Intent(this, SessionManagement.class);
            in = new Intent(this, SessionManagement.class);
            in.putExtra("UserID", userID);
            in.putExtra("SessionID", 0);
            in.putExtra("upd", false);
            section = newSection(
                    "Start new session",
                    in
            );
            this.addSection(section);

            if (owner) {
                if (sessionID != 0) {
                    in = new Intent(this, SessionManagement.class);
                    in.putExtra("UserID", userID);
                    in.putExtra("SessionID", sessionID);
                    in.putExtra("upd", true);
                    section = newSection(
                            "Manage Session",
                            in
                    );
                    this.addSection(section);

                    section = newSection(
                            "Pose Quiz Question",
                            new Intent(this, PoseQuestion.class)
                    );
                    this.addSection(section);
                }
            }
        } else {
            section = newSection(
                    "Login",
                    new Intent(this, MainActivity.class)
            );
            this.addBottomSection(section);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public void done(Mapper.MapperSort mapper) {

    }

    /*@Override
    public void done(Mapper.MapperSort mapper) {

    }*/
}