package org.example.clouddatabase;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		Button load = (Button) findViewById(R.id.loadButton);
		load.setOnClickListener(this);
		Button save = (Button) findViewById(R.id.saveButton);
		save.setOnClickListener(this);
		Button delete = (Button) findViewById(R.id.deleteButton);
		delete.setOnClickListener(this);
		//Parse.initialize(this, "APPLICATION ID", "CLIENT KEY");
		//in the line below you need to input your own client key
		//AND your APP ID KEY. You can find both of these keys
		//when you login to parse.com. Go to account->app keys and choose the
		//application that you want to use for Parse.
		//OF COURSE YOU NEED TO HAVE REGISTERED A NEW APPLICATION IN 
		//PARSE.COM BEFORE YOU CAN GET THOSE KEYS - CLICK IN THE UPPER LEFT
		//CORNER WHERE IT SAYS "SELECT AN APP" AND CHOOSE "CREATE NEW"
		Parse.initialize(this, "og6fbtSxuj0YF3tFxX905AsNlGCE5HnVraKlppnD", "94DExZc71dRCDIIClSy44dIgkry3PuhhyokO5Hy4");		
	
	}



	@Override
	public void onClick(View view) {
		System.out.println("In onClick");
		//Saving new data.
		if (view.getId()==R.id.saveButton)
		{
			//we create a new parseobject - the class will be
			//called "Person"
			ParseObject personObject = new ParseObject("Person");
			//getting our age and name from our input fields
			TextView inputAge = (TextView) findViewById(R.id.age);
			int age = Integer.valueOf(inputAge.getText().toString());
			TextView inputName = (TextView) findViewById(R.id.name);
			//make sure we have a string.
			String name = inputName.getText().toString();
			personObject.put("age", age); //we do NOT need to specify the type here
			personObject.put("name",name);
			personObject.put("nickname", name);
			
			try {
				
				Toast toast = Toast.makeText(getApplicationContext(), "Saving Objects......", Toast.LENGTH_SHORT);
	        	toast.show();
	        	//we could also use this code in the line below:
				// testObject.saveInBackground()
	        	// but now we want to see th results directly, so
	        	//we use testObject.save() instead.
	        	//the saveInBackground will start a new thread in Android
	        	//and then do the saving not in UI thread, which of course
	        	//is a nice thing to keep the app responsive.
	        	personObject.save();
	        	//personObject.saveInBackground();
	        	//show a toast to the user.
				toast = Toast.makeText(getApplicationContext(), "Save done!", Toast.LENGTH_SHORT);
	        	toast.show();
			} catch (ParseException e) { 
				System.out.println("Could not save Parse Object");
				e.printStackTrace();
			}
		}
		//delete data from the cloud.
		else if (view.getId()==R.id.deleteButton)
		{
			final ListView list = (ListView) findViewById(R.id.datalist);
			//choose what kinds of objects we want - in this case "Person"
			//Objects.
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Person");
			//we could add constraints - such as the commented line below
			//query.whereEqualTo("name", "Martin");
			query.findInBackground(new FindCallback<ParseObject>() {
			    public void done(List<ParseObject> parseList, ParseException e) {
			        if (e == null) { //no parse exception, everything is okay
			        	ParseObject.deleteAllInBackground(parseList, new DeleteCallback() {
							
			        		//The done method will be called when the 
			        		//deleteInBackground is finished.
							@Override
							public void done(ParseException exception) {
								
								//just make a toast to the user.
								Toast toast = Toast.makeText(getApplicationContext(), "All objects deleted", Toast.LENGTH_SHORT);
					        	toast.show();
					        	//create a new adapter - it should be empty, because
					        	//we have just deleted all our objects.
					        	ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
								        R.layout.listitem);
								System.out.println("drawing list : "+adapter.getCount());
								//attach the adapter to the ListViwe.
								list.setAdapter(adapter);
								//tell that we have updated data in our adapter (it's empty!!)
								adapter.notifyDataSetChanged();
							}
						});
			        	
	    	
			        } else {
			        	Toast toast = Toast.makeText(getApplicationContext(), "Parse Error during load", Toast.LENGTH_SHORT);
			        	toast.show();
			        }
			    }
			});
		}
		else if (view.getId()==R.id.loadButton)
		{
			final ListView list = (ListView) findViewById(R.id.datalist);
			final ArrayList<String> allPersons = new ArrayList<String>();
			//now read all the persons objects from the cloud
			//we want all Person Objects
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Person");
			//we could add constraints - such as the commented line below
			//query.whereEqualTo("name", "Martin");
			//query.
			
			//start our query in the background so as not to block the
			//UI and make the app "hang"
			query.findInBackground(new FindCallback<ParseObject>() {
				
				//The done method is called, when the data is ready from 
				//the parse cloud.
			    public void done(List<ParseObject> parseList, ParseException e) {
			        if (e == null) { //no parse exception, everything is okay
			        	System.out.println("parse load done: found "+parseList.size());
			        	//go through all our person objects and get relevant
			        	//data. If it cannot find the keys such as "name" then
			        	//the resulting object will be null.
			        	for (ParseObject person : parseList)
			        	{
			        		//notice we here use TYPE INFORMATION and
			        		//use getString() and getInt() - these types
			        		//need to correspond with the types we originally
			        		//saved, otherwise we get an error!!!
			        		String name = person.getString("name");
			        		int age = person.getInt("age");
			        		String result = name+" is "+age+" years"; //create a string
			        		allPersons.add(result); //add to the array list
			        	}
			        	//update our adapter - actually creating a new adapter
			        	ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
						        R.layout.listitem);
			        	//add the entire arraylist to our adapter
						adapter.addAll(allPersons);
						System.out.println("drawing list : "+adapter.getCount());
						//set our adapter to point to the list view
						list.setAdapter(adapter);
						//update our view so the user can see the changes.
						adapter.notifyDataSetChanged();
			        } else {
			        	Toast toast = Toast.makeText(getApplicationContext(), "Parse Error during load", Toast.LENGTH_SHORT);
			        	toast.show();
			        }
			    }
			});
			//
			
			
		}
	}
}
