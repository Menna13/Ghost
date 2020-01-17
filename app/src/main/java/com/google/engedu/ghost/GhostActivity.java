/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();

    /*
    Initialize the dictionary by loading the content of the file in the GhostActivity.onCreate method.
    You can use getAssets().open to access the dictionary file as an InputStream and feed it to the SimpleDictionary's constructor
    to instantiate the dictionary member.
    We've provided the implementation of SimpleDictionary's constructor and the isWord method which is all you need for now.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            InputStream dic = assetManager.open("words.txt");
            dictionary = new SimpleDictionary(dic);
            onStart(null);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
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
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     *
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    /*
    Get the current word fragment
If it has at least 4 characters and is a valid word, declare victory for the user
otherwise if a word can be formed with the fragment as prefix, declare victory for the computer and display a possible word
If a word cannot be formed with the fragment, declare victory for the user
     */

    public void onChallenge(View view) {
        TextView text = (TextView) findViewById(R.id.ghostText);
        TextView label = (TextView) findViewById(R.id.gameStatus);
        String currentText = text.getText().toString();
        if (currentText.length() >= 4 && dictionary.isWord(currentText)) {
            label.setText("YOU WIN!"); //
        } else if (dictionary.getAnyWordStartingWith(currentText)== null) { //is checking if it's not null is enough?
            label.setText("Computer WINS!");
            text.setText(dictionary.getAnyWordStartingWith(currentText));
        } else {
            label.setText("YOU WIN!");
        }
    }

    /*
    computerTurn should get the current word fragment and:

Check if the fragment is a word with at least 4 characters. If so declare victory by updating the game status //who's victory?
Use the dictionary's getAnyWordStartingWith method to get a possible longer word
If such a word doesn't exist (method returns null), challenge the user's fragment and declare victory
If such a word does exist, add the next letter of it to the fragment (remember the substring method in the Java string library)
     */

    private void computerTurn() {
        System.out.println("came to cp turn");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        TextView text = (TextView) findViewById(R.id.ghostText);
        String currentText = text.getText().toString();
        if (currentText.length() >= 4 && dictionary.isWord(currentText)) {
            label.setText("You entered a complete word, Computer Wins!");
        } else {
//            String fullWord = null;
//            System.out.println("Made it here");
            String fullWord = dictionary.getAnyWordStartingWith(currentText); //how can I create random prefix?
            System.out.println(" this is full " + fullWord);
//            System.out.println("Made it after");
            if (fullWord == null | fullWord == "") {
                label.setText("Computer challenged you and wins!");
            } else {
                int length = currentText.length();
                String newText = currentText + fullWord.charAt(length);
                text.setText(newText);
                userTurn = true;
                label.setText(USER_TURN);
            }
        }

    }

    /**
     * Handler for user key presses.
     *
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    /*
    Since we are not using a standard EditText field, we will need to do some keyboard handling.
    Proceed to override the GhostActivity.onKeyUp method.
    If the key that the user pressed is not a letter, default to returning the value of super.onKeyUp().
    Otherwise, add the letter to the word fragment.
    Also check whether the current word fragment is a complete word and, if it is,
    update the game status label to indicate so (this is not the right behavior for the game
    but will allow you to verify that your code is working for now).
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (Character.isLetter(event.getUnicodeChar())) {
            System.out.println("Keystroke registered");
            TextView text = (TextView) findViewById(R.id.ghostText);
            String currentText = text.getText().toString();
            char c = (char) event.getUnicodeChar();
            text.setText(currentText + c);
//            String updatedText = text.getText().toString();
//            System.out.println("Made it to computer turn");
            computerTurn();
//            System.out.println("Made it after computer turn");
        }
        return super.onKeyUp(keyCode, event);
    }
}
