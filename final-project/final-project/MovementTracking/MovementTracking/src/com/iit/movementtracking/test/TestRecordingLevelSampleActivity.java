package com.iit.movementtracking.test;

import android.test.ActivityInstrumentationTestCase;

import com.iit.movementtracking.recordinglevel.RecordingLevelSampleActivity;

public class TestRecordingLevelSampleActivity extends
		ActivityInstrumentationTestCase<RecordingLevelSampleActivity> {

	public TestRecordingLevelSampleActivity(){
		super("com.iit.movementtracking.recordinglevel",RecordingLevelSampleActivity.class);
	}

	public TestRecordingLevelSampleActivity(String pkg,
			Class<RecordingLevelSampleActivity> activityClass) {
		super(pkg, activityClass);
		// TODO Auto-generated constructor stub
	}

	public void testAddBuffer(){
		short[] a= {12,13,14};
		short[] b ={1,2,3,4};
		short[] c={1,2,3,4,12,13,14};
//		assertEquals(7,getActivity().addBuffer(a, b).length);
	}

}
