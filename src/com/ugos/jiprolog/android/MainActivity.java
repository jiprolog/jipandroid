package com.ugos.jiprolog.android;

import java.io.PrintStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPRuntimeException;
import com.ugos.jiprolog.engine.JIPSyntaxErrorException;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPVariable;

public class MainActivity extends Activity {

	private JIPEngine jip;

	private TextView textViewResult;
	private AdView mAdView;
	private PrintStream outs;
	private JIPQuery jipQuery;
	private ArrayList<String> history;
	private AutoCompleteTextView editTextQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jip = JIPrologFactory.newInstance(this);

        textViewResult = (TextView)findViewById(R.id.textViewResult);

        outs = new PrintStream(new TextViewOutputStream(textViewResult));

        jip.setUserOutputStream(outs);

        outs.println(JIPEngine.getInfo());

        history = new ArrayList<String>();

        editTextQuery = (AutoCompleteTextView)findViewById(R.id.editTextQuery);

//        MMSDK.initialize(this);
//        InMobi.initialize(this, "61b72fa35fe34744aa35a5cbe47d234a");

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        ImageView submit = (ImageView)findViewById(R.id.imageViewEnter);


        submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				String goal = editTextQuery.getText().toString();
				if(jipQuery == null || !goal.equals("y"))
				{
					JIPTerm queryTerm = null;

			        // parse query
			        try
			        {
			            queryTerm = jip.getTermParser().parseTerm(goal);

			            outs.println("JIP:-" + goal);

				        // open Query
				        jipQuery = jip.openSynchronousQuery(queryTerm);
			        }
			        catch(JIPSyntaxErrorException ex)
			        {
			            ex.printStackTrace();
			            outs.println(ex.toString());
			        }

			        history.add(goal);

			        editTextQuery.setText("");

			        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
			        		MainActivity.this,
			        		android.R.layout.simple_dropdown_item_1line,
			        		history);


			        editTextQuery.setAdapter(adapter);

				}

				if(jipQuery == null )
					return;

		        // check if there is another solution
		        if(jipQuery.hasMoreChoicePoints())
		        {
		        	JIPTerm solution = null;
		        	try
		        	{
		        		solution = jipQuery.nextSolution();
		        	}
		        	catch(JIPRuntimeException ex)
		        	{
		        		outs.println(ex.getMessage());
				        jipQuery.close();
				        jipQuery = null;
				        return;
		        	}
		        	catch(Exception ex)
		        	{
		        		outs.println(ex.getMessage());
				        jipQuery.close();
				        jipQuery = null;
				        return;
		        	}

		        	if(solution == null)
		        	{
		        		outs.println("No");
				        jipQuery.close();
				        jipQuery = null;
		        	}
		        	else
		        	{
			            // Show Solution
		                outs.println("Yes");

		                JIPVariable[] vars = solution.getVariables();

		                for(int i = 0; i < vars.length; i++)
		                {
		                    if(!vars[i].isAnonymous())
		                    {
		                        outs.println(vars[i].getName() + " = " + vars[i].toString(jip));
		                    }
		                }

		                if(!jipQuery.hasMoreChoicePoints())
		                {
		                	jipQuery.close();
		                	jipQuery = null;
		                }
		                else
		                {
		                    outs.print("more? (y/n) ");
		                    outs.flush();

	//	                    m_consoleView.recordHistory(false);
		                }
		        	}
		        }
		        else
		        {
			        jipQuery.close();
			        jipQuery = null;
		        }

			}
		});


    }

//    private void addResult(String msg)
//    {
//    	final LinearLayout layout = (LinearLayout) View.inflate(this, R.layout.user_text, null);
//
//		TextView txtView = (TextView)layout.findViewById(R.id.textView);
//		textViewResult.setText(textViewResult.getText() + msg);
//
//		final ScrollView scrollview = (ScrollView) viewResult.getParent();
//		scrollview.post(new Runnable() {
//		    @Override
//		    public void run() {
//		    	scrollview.scrollTo(0, layout.getTop());
//				layout.requestFocus();
//		    }
//		});
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_about:
        	Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
