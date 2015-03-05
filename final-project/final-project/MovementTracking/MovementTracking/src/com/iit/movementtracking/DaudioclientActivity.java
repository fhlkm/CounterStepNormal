package com.iit.movementtracking;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.movementtracking.R;

public class DaudioclientActivity extends Activity {
	 
    public static final int MENU_START_ID = Menu.FIRST ;
    public static final int MENU_STOP_ID = Menu.FIRST + 1 ;
    public static final int MENU_EXIT_ID = Menu.FIRST + 2 ;
 
    Button btn_play;
    Button btn_stop;
    protected Saudioserver     m_player ;
    protected Saudioclient     m_recorder ;
 
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btn_stop = (Button)findViewById(R.id.btn_stop);
    
        btn_play = (Button)findViewById(R.id.btn_play);
        btn_stop.setOnClickListener(Listener);
        btn_play.setOnClickListener(Listener);
    }

    View.OnClickListener Listener = new View.OnClickListener() {

    	@Override
    	public void onClick(View v) {
    		switch(v.getId()){
    		case  R.id.btn_stop:
    		{  
    			m_recorder.free() ;
    			m_player.free() ;

    			m_player = null ;
    			m_recorder = null ;
    		}

    		break;
    		case R.id.btn_play:
    		{
    			m_player = new Saudioserver() ;
    			m_recorder = new Saudioclient() ;

    			m_recorder.init() ;
//    			m_player.init() ;
    			

    			m_recorder.start() ;
//    			m_player.start() ;

    		}

    		break;
    		default:
    			break;

    		}

    	}
    };
    public boolean onCreateOptionsMenu(Menu aMenu)
    {
        boolean res = super.onCreateOptionsMenu(aMenu) ;

        aMenu.add(0, MENU_START_ID, 0, "START") ;
        aMenu.add(0, MENU_STOP_ID, 0, "STOP") ;
        aMenu.add(0, MENU_EXIT_ID, 0, "EXIT") ;

        return res ;
    }

    
   
    public boolean onOptionsItemSelected(MenuItem aMenuItem)
    {
        switch (aMenuItem.getItemId()) {
        case MENU_START_ID:
            {
             m_player = new Saudioserver() ;
                m_recorder = new Saudioclient() ;

                m_player.init() ;
                m_recorder.init() ;

                m_recorder.start() ;
                m_player.start() ;
               
            }
            break ;
        case MENU_STOP_ID:
            {  
             m_recorder.free() ;
                m_player.free() ;

                m_player = null ;
                m_recorder = null ;
            }
            break ;
        case MENU_EXIT_ID:
            {
                int pid = android.os.Process.myPid() ;
                android.os.Process.killProcess(pid) ;
            }
            break ;
        default:
            break ;
        }

        return super.onOptionsItemSelected(aMenuItem);
    }
}