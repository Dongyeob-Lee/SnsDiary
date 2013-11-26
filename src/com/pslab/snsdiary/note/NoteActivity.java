package com.pslab.snsdiary.note;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.pslab.jaehwan.api.SAPI;
import android.pslab.jaehwan.object.Feed;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.Settings;
import com.facebook.samples.graphapi.CData;
import com.facebook.samples.graphapi.CustomAdapter;
import com.pslab.snsdiary.daily.Daily;
import com.pslab.snsdiary.dropbox.Dropbox_controler;
import com.pslab.snsdiary.MainActivity;
import com.pslab.snsdiary.R;
import com.pslab.snsdiary.spentools.PreferencesOfOtherOption;
import com.pslab.snsdiary.spentools.PreferencesOfSAMMOption;
import com.pslab.snsdiary.spentools.SPenSDKUtils;
import com.pslab.snsdiary.spentools.SPen_Example_VideoDemoFileList;
import com.pslab.snsdiary.spentools.SubjectSelect;
import com.samsung.samm.common.SObject;
import com.samsung.samm.common.SObjectImage;
import com.samsung.samm.common.SObjectStroke;
import com.samsung.samm.common.SObjectVideo;
import com.samsung.samm.common.SOptionSCanvas;
import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spen.settings.SettingFillingInfo;
import com.samsung.spen.settings.SettingStrokeInfo;
import com.samsung.spen.settings.SettingTextInfo;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.ColorPickerColorChangeListener;
import com.samsung.spensdk.applistener.FileProcessListener;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SCanvasModeChangedListener;
import com.samsung.spensdk.applistener.SPenHoverListener;
import com.samsung.spensdk.applistener.SPenTouchListener;
import com.samsung.spensdk.applistener.SettingStrokeChangeListener;
import com.samsung.spensdk.applistener.SettingViewShowListener;

public class NoteActivity extends Activity {

	private final String TAG = "SnsDiary";
	private final boolean SHOW_LOG = false;
	// =============================
	// setting dialog
	// =============================
	Dialog settingDialog;
	ArrayAdapter<String> settingArrayAdapter;
	final String[] sditems = new String[]{"배경선택","저장","삭제"};
	ListView sdListView;
	// =============================
	// background dialog
	// =============================
	Dialog backgroundDialog;
	ImageButton btn_bg_basic ;
	ImageButton btn_bg_blue;
	ImageButton btn_bg_green;
	ImageButton btn_bg_yellow;
	ImageButton btn_bg_pink;
	ImageButton btn_bg_orange;
	ImageView btn_feedlist_dialog;
	// ==============================
	// Application Identifier Setting
	// "SDK Sample Application 1.0"
	// ==============================
	// remove 'final' to edit AppID
	private String APPLICATION_ID_NAME = "SDK Sample Application";
	private int APPLICATION_ID_VERSION_MAJOR = 1;
	private int APPLICATION_ID_VERSION_MINOR = 0;
	private String APPLICATION_ID_VERSION_PATCHNAME = "Debug";
	// ==============================
	// Activity Request code
	// ==============================
	private final int REQUEST_CODE_INSERT_VIDEO_OBJECT = 99;
	private final int REQUEST_CODE_INSERT_IMAGE_OBJECT = 100;
	private final int REQUEST_CODE_SELECT_IMAGE_BACKGROUND = 106;
	// ==============================
	// Hover Pointer Constants
	// ==============================
	private final int HOVER_POINTER_DEFAULT = 0;
	private final int HOVER_POINTER_SIMPLE_ICON = 1;
	private final int HOVER_POINTER_SIMPLE_DRAWABLE = 2;
	private final int HOVER_POINTER_SPEN = 3;
	private final int HOVER_POINTER_SNOTE = 4;

	private final int HOVER_SHOW_ALWAYS_ONHOVER = 0;
	private final int HOVER_SHOW_ONCE_ONHOVER = 1;

	// ==============================
	// Hover Pointer Constants
	// ==============================
	private final int SIDE_BUTTON_CHANGE_SETTING = 0;
	private final int SIDE_BUTTON_SHOW_SETTING_VIEW = 1;

	// ==============================
	// Insert Object Constants
	// ==============================
	private final int INSERT_IMAGE = 0;
	private final int INSERT_VIDEO = 1;
	private final int INSERT_RECORD = 0;
	private final int PLAY_RECORD = 1;
	private final int DELETE_RECORD = 2;
	private static final int SAVE = 0;
	private static final int CANCEL = 1;
	private static final int DELETE = 2;
	private static final int UPLOAD_TO_FACEBOOK = 5;
	private static final int SETTING = 6;
	// /////////////crop image constants
	private static final int PICK_FROM_CAMERA = 112;
	private static final int PICK_FROM_ALBUM = 113;
	private static final int CROP_FROM_CAMERA = 114;

	private Uri mImageCaptureUri;

	// ==============================
	// Variables
	// ==============================
	Context mContext = null;

	private String mTempAMSFolderPath = null;
	private String mTempAMSAttachFolderPath = null;
	private int mSideButtonStyle;

	private final String DEFAULT_SAVE_PATH = "SPenSDK";
	private final String DEFAULT_ATTACH_PATH = "SPenSDK/attach";
	private final String DEFAULT_FILE_EXT = ".png";

	// private RelativeLayout mViewContainer;
	private FrameLayout mLayoutContainer;
	private RelativeLayout mCanvasContainer;

	// /////////////////sliding layout
	private LinearLayout slidinglayout;
	private SlidingDrawer slidingdrawerright1;
	private ProgressBar mProgCircle;
	private ListView feedListView;
	private Button btn_feedlistback;
	private Button btn_feedlistsync;
	boolean bDownloading = false;
	private ArrayList<Feed> list;
	SAPI sapi = new SAPI();
	private CustomAdapter customfeedlistAdapter;
	private Session session;
	private ArrayList<String> feedIdList = new ArrayList<String>();
	// ///////////////////////////////////
	private SCanvasView mSCanvas;

	private String mAuthorImagePath;
	private String mAuthorImageTempPath;
	private ImageView mAuthorImageView;
	private boolean mMultiSelectionMode = false;
	private int mSettingviewType;
	private int mCanvasHeight;
	private int mCanvasWidth;

	private boolean mbContentsOrientationHorizontal = false;
	private int mPreferenceCheckedItem = 0;
	private boolean mbPreviewBtnClick = false;

	private int mEditorGUIStyle = SCanvasConstants.SCANVAS_GUI_STYLE_NORMAL;
	private boolean mbSingleSelectionFixedLayerMode = false;
	private String currentLanguage = null;

	private EditText et_note_title;
	private TextView tv_daily_yearmonth;
	private TextView tv_daily_date;
	private TextView tv_daily_dayofweek;
	private ImageView iv_diary_subject;
	private ImageView btn_text;
	private ImageView btn_draw;
	private ImageView btn_eraser;
	private ImageView btn_picture;
	private ImageView btn_record;
	private ImageView btn_undo;
	private ImageView btn_redo;
	private ImageView btn_facebook;
	private ImageView btn_note_setting;

	// record
	MediaRecorder recorder;
	MediaPlayer player;
	String recordfilename;
	// /weather
	private ImageView conditionImage;
	private TextView tempTextView;
	String selectedregion = "울산";
	Handler wHandler;
	private Note note;
	private int index;
	private File file;
	private String notename;
	private String dirPath;
	Boolean isNew = false;
	// ////////dropbox
	Dropbox_controler dropbox_controler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ///////////////////////////////
		// ////////sliding //////////////
		// //////////////////////////////
		Window win = getWindow();
		// MyApplication application = (MyApplication)getApplication();
		this.session = Session.getActiveSession();

		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		win.requestFeature(Window.FEATURE_NO_TITLE);
		win.setContentView(R.layout.activity_note);
		LayoutInflater slidinginflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		slidinglayout = (LinearLayout) slidinginflater.inflate(
				R.layout.selectedfeed, null);
		LinearLayout.LayoutParams paramlinear = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		win.addContentView(slidinglayout, paramlinear);
		slidingdrawerright1 = (SlidingDrawer) slidinglayout
				.findViewById(R.id.slidingDrawer2);
		mProgCircle = (ProgressBar) findViewById(R.id.feedprogressBar);
		;
		feedListView = (ListView) slidinglayout.findViewById(R.id.feedlistView);
		btn_feedlistback = (Button) slidinglayout
				.findViewById(R.id.backbtn1);
		btn_feedlistback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//닫기전에 가져와서 저장한다.
				note.getNoteDiaries().get(index).setFeedlist(customfeedlistAdapter.getFeedIdList());
				for(int i=0; i<note.getNoteDiaries().get(index).getFeedlist().size(); i++){
					Log.d("dkdmd?", note.getNoteDiaries().get(index).getFeedlist().get(i));
				}
				slidingdrawerright1.animateClose();
			}
		});
		btn_feedlistsync = (Button) slidinglayout.findViewById(R.id.syncbtn);
		btn_feedlistsync.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mProgCircle.setVisibility(View.VISIBLE);
				if (bDownloading)
					return;
				Thread downloadThread = new Thread() {
					public void run() {
						Log.d("session", session.getAccessToken());
						list = sapi.getUserTimeline("facebook");
						bDownloading = true;
						customfeedlistAdapter = setAdapter();
						mCompleteHandler.sendEmptyMessage(0);
					}
				};
				downloadThread.start();
			}
		});
		// ////////////////////////////////////
		// Thread Strict Mode init
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		mContext = this;

		// ------------------------------------
		// UI Setting
		// ------------------------------------
		et_note_title = (EditText) findViewById(R.id.et_note_title);
		iv_diary_subject = (ImageView) findViewById(R.id.iv_diary_subject);
		// date setting
		tv_daily_yearmonth = (TextView) findViewById(R.id.tv_note_yearmonth);
		tv_daily_date = (TextView) findViewById(R.id.tv_note_date);
		tv_daily_dayofweek = (TextView) findViewById(R.id.tv_note_dayofweek);
		// weather setting
		conditionImage = (ImageView) findViewById(R.id.iv_diary_weather);
		tempTextView = (TextView) findViewById(R.id.tv_diary_temperature);
		// ///////////button ?�결..
		btn_text = (ImageView) findViewById(R.id.btn_text);
		btn_draw = (ImageView) findViewById(R.id.btn_draw);
		btn_eraser = (ImageView) findViewById(R.id.btn_eraser);
		btn_picture = (ImageView) findViewById(R.id.btn_picture);
		btn_record = (ImageView) findViewById(R.id.btn_record);
		btn_facebook = (ImageView) findViewById(R.id.btn_facebook);
		btn_undo = (ImageView) findViewById(R.id.btn_undo);
		btn_redo = (ImageView) findViewById(R.id.btn_redo);
		btn_note_setting = (ImageView) findViewById(R.id.btn_note_setting);
		btn_text.setOnClickListener(mBtnClickListener);
		btn_draw.setOnClickListener(mBtnClickListener);
		btn_eraser.setOnClickListener(mBtnClickListener);
		btn_picture.setOnClickListener(mInsertBtnClickListener);
		btn_record.setOnClickListener(mBtnClickListener);
		btn_facebook.setOnClickListener(mBtnClickListener);
		btn_undo.setOnClickListener(undoNredoBtnClickListener);
		btn_redo.setOnClickListener(undoNredoBtnClickListener);
		btn_note_setting.setOnClickListener(mBtnClickListener);
		iv_diary_subject.setOnClickListener(mBtnClickListener);
		btn_feedlist_dialog = (ImageView)findViewById(R.id.btn_feedlist_dialog_note);
		btn_feedlist_dialog.bringToFront();
		dirPath = getFilesDir().getAbsolutePath();
		File pathFile = new File(dirPath, "record");

		if (!pathFile.exists()) {
			pathFile.mkdir();
		}
		// intent
		Bundle bundle = getIntent().getExtras();
		index = getIntent().getIntExtra("index", 0);
		isNew = getIntent().getBooleanExtra("isnew", false);
		note = (Note) bundle.getParcelable("note_object");
		note = loadNoteFromFile(note);
		notename = note.getYear() + note.getMonth() + note.getDate() + ".xml";
		// file
		file = new File(dirPath, notename);
		if (isNew) {
			et_note_title.setHint("제목을 입력해 주세요");
			recordfilename = "null";
			note.getNoteDiaries().add(new NoteDiary());
			note.getNoteDiaries()
					.get(index)
					.setSCanvasFileName(
							note.getYear() + note.getMonth() + note.getDate()
									+ index + ".png");
			iv_diary_subject.setImageResource(R.drawable.e0);
			note.getNoteDiaries().get(index).setSubject(0);

			wHandler = new Handler();
			Runnable r = new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					parser();
				}
			};
			wHandler.postDelayed(r, 1000);

			// note.getNoteDiaries().get(index).setTemperature(temperature)
		} else {
			et_note_title.setText(note.getNoteDiaries().get(index).getTitle());
			setWeatherImage(selectedregion, note.getNoteDiaries().get(index)
					.getWeatherImg(), note.getNoteDiaries().get(index)
					.getTemperature());
			recordfilename = note.getNoteDiaries().get(index)
					.getRecordFileName();
		}

		// note year,month,dayofweek
		tv_daily_yearmonth.setText(note.getYear() + ", " + note.getMonth());
		tv_daily_date.setText(note.getDate());
		tv_daily_dayofweek.setText(SPenSDKUtils.get_day_of_week(note.getYear(),
				note.getMonth(), note.getDate()));
		// title
		if (note.getNoteDiaries().get(index).getTitle() != null) {
			// tx_title.setText(note.getNoteDiaries().get(index).getTitle());
		}
		// notename

		// /////dropbox �?��??
		if (Dropbox_controler.isValid(this)) {
			dropbox_controler = new Dropbox_controler(this);
		}

		// ------------------------------------
		// Create SCanvasView
		// ------------------------------------
		mLayoutContainer = (FrameLayout) findViewById(R.id.layout_container);
		mCanvasContainer = (RelativeLayout) findViewById(R.id.canvas_container);

		mSCanvas = new SCanvasView(mContext);
		mSCanvas.addedByResizingContainer(mCanvasContainer);

		// ------------------------------------
		// SettingView Setting
		// ------------------------------------
		// Resource Map for Layout & Locale
		HashMap<String, Integer> settingResourceMapInt = SPenSDKUtils
				.getSettingLayoutLocaleResourceMap(true, true, true, true);
		// Talk & Description Setting by Locale
		SPenSDKUtils
				.addTalkbackAndDescriptionStringResourceMap(settingResourceMapInt);
		// Resource Map for Custom font path
		HashMap<String, String> settingResourceMapString = SPenSDKUtils
				.getSettingLayoutStringResourceMap(true, true, true, true);

		// Create Setting View
		mSCanvas.createSettingView(mLayoutContainer, settingResourceMapInt,
				settingResourceMapString);

		// Save current locale
		Configuration config = getBaseContext().getResources()
				.getConfiguration();
		currentLanguage = config.locale.getLanguage();

		// ====================================================================================
		//
		// Set Callback Listener(Interface)
		//
		// ====================================================================================
		// ------------------------------------------------
		// SCanvas Listener
		// ------------------------------------------------
		mSCanvas.setSCanvasInitializeListener(new SCanvasInitializeListener() {
			@Override
			public void onInitialized() {
				// --------------------------------------------
				// Start SCanvasView/CanvasView Task Here
				// --------------------------------------------
				// Place SCanvasView In the Center
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mSCanvas
						.getLayoutParams();
				layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
				mSCanvas.setLayoutParams(layoutParams);

				// Application Identifier Setting
				if (!mSCanvas.setAppID(APPLICATION_ID_NAME,
						APPLICATION_ID_VERSION_MAJOR,
						APPLICATION_ID_VERSION_MINOR,
						APPLICATION_ID_VERSION_PATCHNAME))
					Toast.makeText(mContext, "Fail to set App ID.",
							Toast.LENGTH_LONG).show();

				// Set Title
				if (!mSCanvas.setTitle("SPen-SDK Test"))
					Toast.makeText(mContext, "Fail to set Title.",
							Toast.LENGTH_LONG).show();

				// Set Initial Setting View Size
				mSCanvas.setSettingViewSizeOption(
						SCanvasConstants.SCANVAS_SETTINGVIEW_PEN,
						SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);

				mSCanvas.setSCanvasGUIStyle(mEditorGUIStyle);

				mSCanvas.setSingleSelectionFixedLayerMode(mbSingleSelectionFixedLayerMode);
				// if(mbSingleSelectionFixedLayerMode)
				// mSelectionModeBtn.setVisibility(View.GONE);
				mSCanvas.setFingerControlPenDrawing(false);
				mCanvasWidth = mSCanvas.getWidth();
				mCanvasHeight = mSCanvas.getHeight();
				// Get the direction of contents(Canvas)
				if (mCanvasWidth > mCanvasHeight)
					mbContentsOrientationHorizontal = true;
				else
					mbContentsOrientationHorizontal = false;

				applyOtherOption();
				if (!isNew) {
					loadSAMMFile(loadSAMMDataString(note));
					// ///?????�� �?��????�� ?��?.

				} else {
					mSCanvas.setBGImage(BitmapFactory.decodeResource(getResources(), R.drawable.backgroud_subject_empty));
				}

				// Update button state
			}
		});

		// ------------------------------------------------
		// History Change Listener
		// ------------------------------------------------
		mSCanvas.setHistoryUpdateListener(new HistoryUpdateListener() {
			@Override
			public void onHistoryChanged(boolean undoable, boolean redoable) {
				btn_undo.setEnabled(undoable);
				btn_redo.setEnabled(redoable);
			}
		});

		// ------------------------------------------------
		// SCanvas Mode Changed Listener
		// ------------------------------------------------
		mSCanvas.setSCanvasModeChangedListener(new SCanvasModeChangedListener() {

			@Override
			public void onModeChanged(int mode) {
				if (!(mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_SELECT)) {
					mMultiSelectionMode = false;
				} else {
					if (mSCanvas.isMultiSelectionMode())
						mMultiSelectionMode = true;
					else
						mMultiSelectionMode = false;
				}
				// updateSelectButton();
			}

			@Override
			public void onMovingModeEnabled(boolean bEnableMovingMode) {
			}

			@Override
			public void onColorPickerModeEnabled(boolean bEnableColorPickerMode) {
			}
		});
		// //////////////////////////////////////////
		// ///////delete stroke change listner//////
		// //////////////////////////////////////////
		// ------------------------------------------------
		// Color Picker Listener
		// ------------------------------------------------
		mSCanvas.setColorPickerColorChangeListener(new ColorPickerColorChangeListener() {
			@Override
			public void onColorPickerColorChanged(int nColor) {

				int nCurMode = mSCanvas.getCanvasMode();
				if (nCurMode == SCanvasConstants.SCANVAS_MODE_INPUT_PEN) {
					SettingStrokeInfo strokeInfo = mSCanvas
							.getSettingViewStrokeInfo();
					if (strokeInfo != null) {
						strokeInfo.setStrokeColor(nColor);
						mSCanvas.setSettingViewStrokeInfo(strokeInfo);
					}
				} else if (nCurMode == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER) {
					// do nothing
				} else if (nCurMode == SCanvasConstants.SCANVAS_MODE_INPUT_TEXT) {
					SettingTextInfo textInfo = mSCanvas
							.getSettingViewTextInfo();
					if (textInfo != null) {
						textInfo.setTextColor(nColor);
						mSCanvas.setSettingViewTextInfo(textInfo);
					}
				} else if (nCurMode == SCanvasConstants.SCANVAS_MODE_INPUT_FILLING) {
					SettingFillingInfo fillingInfo = mSCanvas
							.getSettingViewFillingInfo();
					if (fillingInfo != null) {
						fillingInfo.setFillingColor(nColor);
						mSCanvas.setSettingViewFillingInfo(fillingInfo);
					}
				}
			}
		});

		// --------------------------------------------
		// Set S pen Touch Listener
		// --------------------------------------------
		mSCanvas.setSPenTouchListener(new SPenTouchListener() {

			@Override
			public boolean onTouchFinger(View view, MotionEvent event) {
				return false;
			}

			@Override
			public boolean onTouchPen(View view, MotionEvent event) {
				return false;
			}

			@Override
			public boolean onTouchPenEraser(View view, MotionEvent event) {
				return false;
			}

			@Override
			public void onTouchButtonDown(View view, MotionEvent event) {
			}

			@Override
			public void onTouchButtonUp(View view, MotionEvent event) {
				showObjectPopUpMenu((int) event.getX(), (int) event.getY());
			}

		});

		// --------------------------------------------
		// Set S pen HoverListener
		// --------------------------------------------
		mSCanvas.setSPenHoverListener(new SPenHoverListener() {

			boolean isPenButtonDown = false;

			@Override
			public boolean onHover(View view, MotionEvent event) {
				return false;
			}

			@Override
			public void onHoverButtonDown(View view, MotionEvent event) {
				isPenButtonDown = true;
			}

			@Override
			public void onHoverButtonUp(View view, MotionEvent event) {
				if (isPenButtonDown == false) // ignore button up event if
												// button was not pressed on
												// hover
					return;
				isPenButtonDown = false;

				// --------------------------------------------------------------
				// Show popup menu if the object is selected or if sobject exist
				// in clipboard
				// --------------------------------------------------------------
				if (mSCanvas.isSObjectSelected()
						|| mSCanvas.isClipboardSObjectListExist()) {
					showObjectPopUpMenu((int) event.getX(), (int) event.getY());
				} else {
					if (!mSCanvas.isVideoViewExist()) {
						if (mSideButtonStyle == SIDE_BUTTON_CHANGE_SETTING) {

							boolean bIncludeDefinedSetting = true;
							boolean bIncludeCustomSetting = true;
							boolean bIncludeEraserSetting = true;
							SettingStrokeInfo settingInfo = mSCanvas
									.getSettingViewNextStrokeInfo(
											bIncludeDefinedSetting,
											bIncludeCustomSetting,
											bIncludeEraserSetting);

							if (settingInfo != null) {
								mSCanvas.setSettingViewStrokeInfo(settingInfo);
								int nPreviousMode = mSCanvas.getCanvasMode();
								// Mode Change : Pen => Eraser
								if (nPreviousMode == SCanvasConstants.SCANVAS_MODE_INPUT_PEN
										&& settingInfo.getStrokeStyle() == SObjectStroke.SAMM_STROKE_STYLE_ERASER) {
									// Change Mode
									mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
									// Show Setting View
									if (mSCanvas
											.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN)) {
										mSCanvas.showSettingView(
												SCanvasConstants.SCANVAS_SETTINGVIEW_PEN,
												false);
										mSCanvas.showSettingView(
												SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER,
												true);
									}
								}
								// Mode Change : Eraser => Pen
								if (nPreviousMode == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER
										&& settingInfo.getStrokeStyle() != SObjectStroke.SAMM_STROKE_STYLE_ERASER) {
									// Change Mode
									mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
									// Show Setting View
									if (mSCanvas
											.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER)) {
										mSCanvas.showSettingView(
												SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER,
												false);
										mSCanvas.showSettingView(
												SCanvasConstants.SCANVAS_SETTINGVIEW_PEN,
												true);
									}
								}
							}
						} // end of if(mSideButtonStyle ==
							// SIDE_BUTTON_CHANGE_SETTING){
							// Show SettingView(Toggle SettingView)
						else if (mSideButtonStyle == SIDE_BUTTON_SHOW_SETTING_VIEW) {
							doHoverButtonUp((int) event.getX(),
									(int) event.getY());
						}
					}
				} // end of else // if(mSCanvas.isSObjectSelected() ||
					// mSCanvas.isClipboardSObjectListExist()){
			} // end of onHoverButtonUp
		});

		// Update UI
		// mUndoBtn.setEnabled(false);
		// mRedoBtn.setEnabled(false);
		btn_undo.setEnabled(false);
		btn_redo.setEnabled(false);
		// mPenBtn.setSelected(true);

		// create basic save/road file path
		File sdcard_path = Environment.getExternalStorageDirectory();
		File default_path = new File(sdcard_path, DEFAULT_SAVE_PATH);
		if (!default_path.exists()) {
			if (!default_path.mkdirs()) {
				Log.e(TAG, "Default Save Path Creation Error");
				return;
			}
		}

		// attach file path
		File spen_attach_path = new File(sdcard_path, DEFAULT_ATTACH_PATH);
		if (!spen_attach_path.exists()) {
			if (!spen_attach_path.mkdirs()) {
				Log.e(TAG, "Default Attach Path Creation Error");
				return;
			}
		}

		mTempAMSFolderPath = default_path.getAbsolutePath();
		mTempAMSAttachFolderPath = spen_attach_path.getAbsolutePath();

		// Caution:
		// Do NOT load file or start animation here because we don't know canvas
		// size here.
		// Start such SCanvasView Task at onInitialized() of
		// SCanvasInitializeListener
		Toast.makeText(this,
				"subject Id:" + note.getNoteDiaries().get(index).getSubject(),
				Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Release SCanvasView resources
		if (!mSCanvas.closeSCanvasView())
			Log.e(TAG, "Fail to close SCanvasView");
		mSCanvas.clearSCanvasView();
	}

	@Override
	public void onBackPressed() {
		SPenSDKUtils.alertActivityFinish(this, "Exit");
//		mSCanvas.clearSCanvasView();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (mSCanvas.isVideoViewExist()) {
			mSCanvas.closeSAMMVideoView();
		}

		if (!newConfig.locale.getLanguage().equals(currentLanguage)) {
			// Recreate SettingView to text string as locale
			mSCanvas.recreateSettingView();
			currentLanguage = newConfig.locale.getLanguage();
		}

		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (Dropbox_controler.isValid(this)) {
			dropbox_controler.resume();
		}
	}

	// // redo, undo clicklistener
	private OnClickListener undoNredoBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(btn_undo)) {
				mSCanvas.undo();
			} else if (v.equals(btn_redo)) {
				mSCanvas.redo();
			}
			btn_undo.setEnabled(mSCanvas.isUndoable());
			btn_redo.setEnabled(mSCanvas.isRedoable());
		}
	};
	private OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int nBtnID = v.getId();
			// If the mode is not changed, open the setting view. If the mode is
			// same, close the setting view.
			if (nBtnID == btn_draw.getId()) {
				if (mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_PEN) {
					mSCanvas.setSettingViewSizeOption(
							SCanvasConstants.SCANVAS_SETTINGVIEW_PEN,
							SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
				} else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
					mSCanvas.showSettingView(
							SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);
				}
			} else if (nBtnID == btn_eraser.getId()) {
				if (mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER) {
					mSCanvas.setSettingViewSizeOption(
							SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER,
							SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
				} else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
					mSCanvas.showSettingView(
							SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
				}
			} else if (nBtnID == btn_text.getId()) {
				if (mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_TEXT) {
					mSCanvas.setSettingViewSizeOption(
							SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT,
							SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT);
				} else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
					mSCanvas.showSettingView(
							SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, false);
					Toast.makeText(mContext, "Tap Canvas to insert Text",
							Toast.LENGTH_SHORT).show();
				}
			} else if (nBtnID == btn_facebook.getId()) {
				slidingdrawerright1.animateOpen();
			} else if (nBtnID == btn_note_setting.getId()) {
				settingArrayAdapter = new ArrayAdapter<String>(NoteActivity.this, android.R.layout.simple_list_item_activated_1,sditems);
				settingDialog = new Dialog(NoteActivity.this);
				settingDialog.setContentView(R.layout.setting_dialog);
				settingDialog.setTitle("설정");
				settingDialog.setCancelable(true);
				sdListView = (ListView)settingDialog.findViewById(R.id.lv_note_setting_dialog);
				sdListView.setAdapter(settingArrayAdapter);
				
				sdListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int pos, long arg3) {
						if(pos==0){
							//배경선택
							showSelectBgDialog();
							settingDialog.dismiss();
						}else if(pos==1){
							//저장
							showSaveDialog();
							settingDialog.dismiss();
						}else if(pos==2){
							//삭제
							showDeleteDialog();
							settingDialog.dismiss();
						}
					}
					
				});
				
				Button btn_setting_dialog = (Button)settingDialog.findViewById(R.id.btn_note_setting_dialog);
				btn_setting_dialog.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						settingDialog.dismiss();
					}
				});
				
				settingDialog.show();
			} else if (nBtnID == iv_diary_subject.getId()) {
				Log.d("subject", "select");

				CustomDialog customDialog = new CustomDialog(NoteActivity.this);
				customDialog.show();

			} else if (nBtnID == btn_record.getId()) {
				String items[] = { "녹음하기", "녹음듣기", "녹음삭제" };
				AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
				ad.setIcon(getResources().getDrawable(
						android.R.drawable.ic_dialog_alert)); // Android
																// Resource
				ad.setTitle(getResources().getString(R.string.app_name))
						.setItems(items, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								switch (which) {
								case INSERT_RECORD:
									if (!recordfilename.equals("null")) {
										Log.d("record", recordfilename);
										AlertDialog.Builder builder = new AlertDialog.Builder(
												NoteActivity.this);

										builder.setTitle("Record");
										builder.setMessage("이미 녹음된 파일이 있읍니다. 새로 녹음 하시겠습니까?");
										builder.setPositiveButton(
												"Yes",
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface arg0,
															int arg1) {
														// 녹음 시

														AlertDialog.Builder recordbuilder = new AlertDialog.Builder(
																NoteActivity.this);
														recordbuilder
																.setTitle("녹음중");
														recordbuilder
																.setMessage("녹음중");
														recordbuilder
																.setPositiveButton(
																		"녹음 끝",
																		new DialogInterface.OnClickListener() {

																			@Override
																			public void onClick(
																					DialogInterface dialog,
																					int which) {
																				// TODO
																				// Auto-generated
																				// method
																				// stub
																				recordStop();
																			}
																		});
														recordStart();
														recordbuilder.show();
													}
												});
										builder.setNegativeButton("No", null);
										builder.show();
									} else {
										// 녹음 시작.
										recordfilename = note.getYear()
												+ note.getMonth()
												+ note.getDate() + index
												+ ".mp4";
										Log.d("record", recordfilename);
										AlertDialog.Builder recordbuilder = new AlertDialog.Builder(
												NoteActivity.this);
										recordbuilder.setTitle("녹음중");
										recordbuilder.setMessage("녹음중");
										recordbuilder
												.setPositiveButton(
														"녹음 끝",
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																// TODO
																// Auto-generated
																// method stub
																recordStop();
															}
														});
										recordStart();
										recordbuilder.show();
									}

									break;
								case PLAY_RECORD:
									if (recordfilename.equals("null")) {
										Toast.makeText(NoteActivity.this,
												"녹음된 파일이 없습니다.",
												Toast.LENGTH_LONG).show();
									} else {
										playStart();
										Log.d("record", recordfilename);
									}

									break;
								case DELETE_RECORD:
									if (recordfilename.equals("null")) {
										Toast.makeText(NoteActivity.this,
												"녹음된 파일이 없습니다.",
												Toast.LENGTH_LONG).show();
									} else {
										Log.d("record", recordfilename);
										deleteRecord();
									}
									break;
								}
								dialog.dismiss();
							}
						}).show();
			}else if(nBtnID == btn_feedlist_dialog.getId()){
			}
		}
	};
	
	//save dialog
	private void showSaveDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(
				NoteActivity.this);

		builder.setTitle("저장");
		builder.setMessage("저장하시겠습니까?");

		builder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						String title;
						if(et_note_title.getText().toString().equals("")){
							title="제목을 입력해주십시오";
						}else{
							title = et_note_title.getText().toString();
						}
						note.getNoteDiaries()
								.get(index)
								.setTitle(
										title);
						note.getNoteDiaries().get(index).setRecordFileName(recordfilename);
						checkSameSaveFileName(getFilesDir()
								.getAbsoluteFile()
								+ "/"
								+ note.getNoteDiaries().get(index)
										.getSCanvasFileName());
						createNoteFile(file);
						if (Dropbox_controler
								.isValid(NoteActivity.this)) {
							upload_to_dropbox();
						}
						Intent intent = new Intent(NoteActivity.this,
								Daily.class);
						intent.putExtra("note_object", note);
						intent.putExtra("index", index);
						startActivity(intent);
						finish();
					}
				});
		builder.setNegativeButton("No", null);
		builder.show();
	}
	//delete dialog
	private void showDeleteDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(
				NoteActivity.this);

		builder.setTitle("삭제");
		builder.setMessage("삭제하시겠습니까?");

		builder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (note.getNoteDiaries().size() == 1) {
							if (Dropbox_controler
									.isValid(NoteActivity.this)) {
								delete_xml_dropbox();
							}
							deletePngFile();
							deleteRecord();
							delete_cur_file();
						} else {
							deletePngFile();
							deleteRecord();
							note.getNoteDiaries().remove(index);
							if (Dropbox_controler
									.isValid(NoteActivity.this)) {
								upload_to_dropbox();
							}
							createNoteFile(file);
							if(index!=0){
								index--;
							}
						}
					}
				});
		builder.setNegativeButton("No", null);
		builder.show();
	}
	private void showSelectBgDialog(){
		backgroundDialog = new Dialog(NoteActivity.this);
		backgroundDialog.setContentView(R.layout.background_choose_dialog);
		backgroundDialog.setTitle("설정");
		backgroundDialog.setCancelable(true);
		Button button = (Button)backgroundDialog.findViewById(R.id.btn_background_select_exit);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				backgroundDialog.dismiss();
			}
		});
		btn_bg_basic = (ImageButton)backgroundDialog.findViewById(R.id.btn_background_select_basic);
		btn_bg_blue = (ImageButton)backgroundDialog.findViewById(R.id.btn_background_select_blue);
		btn_bg_green = (ImageButton)backgroundDialog.findViewById(R.id.btn_background_select_green);
		btn_bg_yellow = (ImageButton)backgroundDialog.findViewById(R.id.btn_background_select_yellow);
		btn_bg_pink = (ImageButton)backgroundDialog.findViewById(R.id.btn_background_select_pink);
		btn_bg_orange = (ImageButton)backgroundDialog.findViewById(R.id.btn_background_select_orange);
		btn_bg_basic.setOnClickListener(bgselectClickListener);
		btn_bg_blue.setOnClickListener(bgselectClickListener);
		btn_bg_green.setOnClickListener(bgselectClickListener);
		btn_bg_yellow.setOnClickListener(bgselectClickListener);
		btn_bg_pink.setOnClickListener(bgselectClickListener);
		btn_bg_orange.setOnClickListener(bgselectClickListener);
		backgroundDialog.show();
	}
	OnClickListener bgselectClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int bgId = v.getId();
			if(bgId == btn_bg_basic.getId()){
				mSCanvas.setBGImage(BitmapFactory.decodeResource(getResources(), R.drawable.backgroud_subject_empty));
			}else if(bgId == btn_bg_blue.getId()){
				mSCanvas.setBGImage(BitmapFactory.decodeResource(getResources(), R.drawable.backgroud_subject_blue));
			}else if(bgId == btn_bg_green.getId()){
				mSCanvas.setBGImage(BitmapFactory.decodeResource(getResources(), R.drawable.backgroud_subject_diet));
			}else if(bgId == btn_bg_yellow.getId()){
				mSCanvas.setBGImage(BitmapFactory.decodeResource(getResources(), R.drawable.backgroud_subject_common));
			}else if(bgId == btn_bg_pink.getId()){
				mSCanvas.setBGImage(BitmapFactory.decodeResource(getResources(), R.drawable.backgroud_subject_love));
			}else if(bgId == btn_bg_orange.getId()){
				mSCanvas.setBGImage(BitmapFactory.decodeResource(getResources(), R.drawable.backgroud_subject_orange));
			}
			backgroundDialog.dismiss();
		}
	};
	private void deletePngFile() {
		File pngFile = new File(dirPath+"/image",note.getNoteDiaries().get(index).getSCanvasFileName());
		if(pngFile.exists()){
			pngFile.delete();
		}
	}
	// insert video , image
	private OnClickListener mInsertBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String items[] = { "Image", "Video" };
			AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
			ad.setIcon(getResources().getDrawable(
					android.R.drawable.ic_dialog_alert)); // Android Resource
			ad.setTitle(getResources().getString(R.string.app_name))
					.setItems(items, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							switch (which) {
							case INSERT_IMAGE:
								callGalleryForInputImage(REQUEST_CODE_INSERT_IMAGE_OBJECT);
								break;
							case INSERT_VIDEO:
								// callGalleryForInputVideo(REQUEST_CODE_INSERT_VIDEO_OBJECT);
								insertVideoObjectSelection();
								break;
							}
							dialog.dismiss();
						}
					}).show();
		}
	};

	private void insertVideoObjectSelection() {

		String items[] = { "Video file", "Video URL Link" };
		AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
		ad.setIcon(getResources().getDrawable(
				android.R.drawable.ic_dialog_alert)); // Android Resource
		ad.setTitle(getResources().getString(R.string.app_name))
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						switch (which) {
						case 0: // Video file
							Intent intent = new Intent(NoteActivity.this,
									SPen_Example_VideoDemoFileList.class);
							startActivityForResult(intent,
									REQUEST_CODE_INSERT_VIDEO_OBJECT);
							// callGalleryForInputVideo(REQUEST_CODE_INSERT_VIDEO_OBJECT);
							break;
						case 1: // Video URL Link
							LayoutInflater factory = LayoutInflater
									.from(NoteActivity.this);
							final View textEntryView = factory.inflate(
									R.layout.alert_dialog_get_hypertext, null);
							TextView textTitle = (TextView) textEntryView
									.findViewById(R.id.textTitle);
							textTitle
									.setText("Enter video url here(e.g.http://www.youtube.com/watch?v=VIDEOID");

							// Set the default value
							String videoURL = "http://www.youtube.com/watch?v=oC6OVqkcB7I";
							// String videoURL =
							// "http://sports.news.naver.com/videoCenter/index.nhn?uCategory=wfootball&id=36020";
							EditText et = (EditText) textEntryView
									.findViewById(R.id.text);
							et.setText(videoURL);

							AlertDialog dlg = new AlertDialog.Builder(
									NoteActivity.this)
									.setTitle("Video URL")
									.setView(textEntryView)
									.setPositiveButton(
											"Done",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {
													EditText et = (EditText) textEntryView
															.findViewById(R.id.text);
													String videoURL = et
															.getText()
															.toString();
													addVideoURLObject(videoURL);
												}
											})
									.setNegativeButton(
											"Cancel",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {
													/*
													 * User clicked cancel so do
													 * some stuff
													 */
												}
											}).create();
							dlg.getWindow()
									.setSoftInputMode(
											WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
													| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
							dlg.show();

							break;
						}
						dialog.dismiss();
					}
				}).show();
	}

	private boolean addVideoFileObject(String strVideoFile) {
		Bitmap bmpThumbnail = ThumbnailUtils.createVideoThumbnail(strVideoFile,
				Thumbnails.MINI_KIND);
		if (bmpThumbnail == null) {
			Toast.makeText(this, "Extract video thumbnail Fail!",
					Toast.LENGTH_LONG).show();
			return false;
		}

		RectF rectF = getVideoObjectDefaultRect(bmpThumbnail, false);
		SObjectVideo sVideoObject = new SObjectVideo();
		sVideoObject.setRect(rectF);
		if (bmpThumbnail.isMutable())
			sVideoObject.setThumbnailImageBitmap(bmpThumbnail.copy(
					Bitmap.Config.ARGB_8888, false));
		else
			sVideoObject.setThumbnailImageBitmap(bmpThumbnail);

		sVideoObject.setStyle(SObjectVideo.SAMM_VIDEOSTYLE_NORMAL);
		sVideoObject.setVideoPath(strVideoFile);
		return addSAMMVideo(sVideoObject);
	}

	private boolean addVideoURLObject(String strVideoURL) {
		// Temporary
		Bitmap bmpThumbnail = BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.video_link);
		RectF rectF = getVideoObjectDefaultRect(bmpThumbnail, true);
		SObjectVideo sVideoObject = new SObjectVideo();
		sVideoObject.setRect(rectF);
		sVideoObject.setThumbnailImageBitmap(bmpThumbnail);
		sVideoObject.setStyle(SObjectVideo.SAMM_VIDEOSTYLE_URL);
		sVideoObject.setVideoURL(strVideoURL);
		return addSAMMVideo(sVideoObject);
	}

	private boolean addSAMMVideo(SObjectVideo sVideoObject) {
		if (mSCanvas.insertSAMMVideo(sVideoObject, true)) {
			Toast.makeText(this, "Insert video Success!", Toast.LENGTH_SHORT)
					.show();
			return true;
		} else {
			Toast.makeText(this, "Insert video Fail!", Toast.LENGTH_LONG)
					.show();
			return false;
		}
	}

	RectF getDefaultRect(String imgBitmap) {
		// Rect Region : Consider image real size
		BitmapFactory.Options opts = SPenSDKUtils.getBitmapSize(imgBitmap);
		int nImageWidth = opts.outWidth;
		int nImageHeight = opts.outHeight;
		int nScreenWidth = mSCanvas.getWidth();
		int nScreenHeight = mSCanvas.getHeight();
		int nBoxRadius = (nScreenWidth > nScreenHeight) ? nScreenHeight / 4
				: nScreenWidth / 4;
		int nCenterX = nScreenWidth / 2;
		int nCenterY = nScreenHeight / 2;
		if (nImageWidth > nImageHeight)
			return new RectF(nCenterX - nBoxRadius, nCenterY
					- (nBoxRadius * nImageHeight / nImageWidth), nCenterX
					+ nBoxRadius, nCenterY
					+ (nBoxRadius * nImageHeight / nImageWidth));
		else
			return new RectF(nCenterX
					- (nBoxRadius * nImageWidth / nImageHeight), nCenterY
					- nBoxRadius, nCenterX
					+ (nBoxRadius * nImageWidth / nImageHeight), nCenterY
					+ nBoxRadius);
	}

	RectF getVideoObjectDefaultRect(Bitmap videoThumbnail, boolean bVideoLink) {
		if (videoThumbnail == null)
			return null;
		// Rect Region : Consider image real size
		int nImageWidth = videoThumbnail.getWidth();
		int nImageHeight = videoThumbnail.getHeight();
		int nScreenWidth = mSCanvas.getWidth();
		int nScreenHeight = mSCanvas.getHeight();
		int nBoxRadius;
		if (bVideoLink)
			nBoxRadius = (nScreenWidth > nScreenHeight) ? nScreenHeight / 8
					: nScreenWidth / 8;
		else
			nBoxRadius = (nScreenWidth > nScreenHeight) ? nScreenHeight / 3
					: nScreenWidth / 3;
		int nCenterX = nScreenWidth / 2;
		int nCenterY = nScreenHeight / 2;
		if (nImageWidth > nImageHeight)
			return new RectF(nCenterX - nBoxRadius, nCenterY
					- (nBoxRadius * nImageHeight / nImageWidth), nCenterX
					+ nBoxRadius, nCenterY
					+ (nBoxRadius * nImageHeight / nImageWidth));
		else
			return new RectF(nCenterX
					- (nBoxRadius * nImageWidth / nImageHeight), nCenterY
					- nBoxRadius, nCenterX
					+ (nBoxRadius * nImageWidth / nImageHeight), nCenterY
					+ nBoxRadius);
	}

	void deleteSelectedSObject() {
		if (!mSCanvas.deleteSelectedSObject()) {
			Toast.makeText(mContext, "Fail to delete object list.",
					Toast.LENGTH_LONG).show();
		}
	}

	void copySelectedObject() {
		boolean bResetClipboard = true;
		if (!mSCanvas.copySelectedSObjectList(bResetClipboard)) {
			Toast.makeText(mContext, "Fail to copy selected object list.",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(mContext, "Object copied.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	void cutSelectedObject() {
		boolean bResetClipboard = true;
		if (!mSCanvas.cutSelectedSObjectList(bResetClipboard)) {
			Toast.makeText(mContext, "Fail to cut selected object list.",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(mContext, "Object cut.", Toast.LENGTH_SHORT).show();
		}
	}

	void clearClipboardObject() {
		mSCanvas.clearClipboardSObjectList();
		Toast.makeText(mContext, "Clipboard cleared.", Toast.LENGTH_SHORT)
				.show();
	}

	void pasteClipboardObject(int nEventPositionX, int nEventPositionY) {
		boolean bSelectObject = true;
		// mapping to the matrix
		PointF mapPoint = mSCanvas.mapSCanvasPoint(new PointF(nEventPositionX,
				nEventPositionY));
		int nMappedEventX = (int) mapPoint.x;
		int nMappedEventY = (int) mapPoint.y;
		if (!mSCanvas.pasteClipboardSObjectList(bSelectObject, nMappedEventX,
				nMappedEventY)) {
			Toast.makeText(mContext, "Fail to paste clipboard object list.",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(mContext, "Object pasted.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	boolean loadSAMMFile(String strFileName) {
		if (mSCanvas.isAnimationMode()) {
			// It must be not animation mode.
		} else {
			// set progress dialog
			mSCanvas.setProgressDialogSetting(R.string.load_title,
					R.string.load_msg, ProgressDialog.STYLE_HORIZONTAL, false);

			// canvas option setting
			SOptionSCanvas canvasOption = mSCanvas.getOption();
			if (canvasOption == null)
				return false;
			canvasOption.mSAMMOption
					.setConvertCanvasSizeOption(PreferencesOfSAMMOption
							.getPreferenceLoadCanvasSize(mContext));
			canvasOption.mSAMMOption
					.setConvertCanvasHorizontalAlignOption(PreferencesOfSAMMOption
							.getPreferenceLoadCanvasHAlign(mContext));
			canvasOption.mSAMMOption
					.setConvertCanvasVerticalAlignOption(PreferencesOfSAMMOption
							.getPreferenceLoadCanvasVAlign(mContext));
			canvasOption.mSAMMOption
					.setDecodePriorityFGData(PreferencesOfSAMMOption
							.getPreferenceDecodePriorityFGData(mContext));
			// option setting
			mSCanvas.setOption(canvasOption);

			// show progress for loading data
			if (mSCanvas.loadSAMMFile(strFileName, true, true, true)) {
				// Loading Result can be get by callback function
			} else {
				Toast.makeText(this,
						"Load AMS File(" + strFileName + ") Fail!",
						Toast.LENGTH_LONG).show();
				return false;
			}
		}
		return true;
	}

	boolean saveSAMMFile(String strFileName, boolean bShowSuccessLog) {
		if (mSCanvas.saveSAMMFile(strFileName)) {
			if (bShowSuccessLog) {
				Toast.makeText(this,
						"Save AMS File(" + strFileName + ") Success!",
						Toast.LENGTH_LONG).show();
			}
			return true;
		} else {
			Toast.makeText(this, "Save AMS File(" + strFileName + ") Fail!",
					Toast.LENGTH_LONG).show();
			return false;
		}
	}

	private void checkSameSaveFileName(final String saveFileName) {

		File fSaveFile = new File(saveFileName);
		if (fSaveFile.exists()) {

			SOptionSCanvas canvasOption = new SOptionSCanvas();
			// medium size : to reduce saving time
			canvasOption.mSAMMOption.setSaveImageSize(PreferencesOfSAMMOption
					.getPreferenceSaveImageSize(mContext));
			// canvasOption.mSaveOption.setSaveImageSize(SOptionSAMM.SAMM_SAVE_OPTION_MEDIUM_SIZE);
			// valid only to save jpg
			// canvasOption.mSAMMOption.setJPGImageQuality(100);
			// Cropping option
			canvasOption.mSAMMOption
					.setSaveImageLeftCroppingOption(PreferencesOfSAMMOption
							.getPreferenceSaveImageHorizontalCrop(mContext));
			canvasOption.mSAMMOption
					.setSaveImageRightCroppingOption(PreferencesOfSAMMOption
							.getPreferenceSaveImageHorizontalCrop(mContext));
			canvasOption.mSAMMOption
					.setSaveImageTopCroppingOption(PreferencesOfSAMMOption
							.getPreferenceSaveImageVerticalCrop(mContext));
			canvasOption.mSAMMOption
					.setSaveImageBottomCroppingOption(PreferencesOfSAMMOption
							.getPreferenceSaveImageVerticalCrop(mContext));
			canvasOption.mSAMMOption
					.setSaveContentsCroppingOption(PreferencesOfSAMMOption
							.getPreferenceSaveContentsCrop(mContext));
			// content quality minimum
			canvasOption.mSAMMOption.setContentsQuality(PreferencesOfSAMMOption
					.getPreferenceSaveImageQuality(mContext));
			// canvasOption.mSAMMOption.setContentsQuality(SOptionSAMM.SAMM_CONTENTS_QUALITY_MINIMUM);
			// with background(image, color) set
			canvasOption.mSAMMOption
					.setSaveOnlyForegroundImage(false);
			if(canvasOption.mSAMMOption.isSaveOnlyForegroundImage()){
				Log.d("background", "true");
			}else{
				Log.d("background", "false");
			}
//			canvasOption.mSAMMOption.setSaveOnlyForegroundImage(true); //
			// with background(image, color) set
			// canvasOption.mSAMMOption.setSaveOnlyForegroundImage(true); // no
			// background
			// Create new image file to save
			canvasOption.mSAMMOption
					.setCreateNewImageFile(PreferencesOfSAMMOption
							.getPreferenceSaveCreateNewImageFile(mContext));
			canvasOption.mSAMMOption
					.setEncodeForegroundImage(PreferencesOfSAMMOption
							.getPreferenceEncodeForegroundImageFile(mContext));
			canvasOption.mSAMMOption
					.setEncodeThumbnailImage(PreferencesOfSAMMOption
							.getPreferenceEncodeThumbnailImageFile(mContext));
			canvasOption.mSAMMOption
					.setEncodeObjectData(PreferencesOfSAMMOption
							.getPreferenceEncodeObjectDataFile(mContext));
			canvasOption.mSAMMOption
					.setEncodeVideoFileDataOption(PreferencesOfSAMMOption
							.getPreferenceEncodeVideoFileData(mContext));
			// option setting
			mSCanvas.setOption(canvasOption);
			saveSAMMFile(saveFileName, true);

		} else {
			// canvas option setting
			SOptionSCanvas canvasOption = new SOptionSCanvas();
			// Cropping option
			canvasOption.mSAMMOption
					.setSaveImageLeftCroppingOption(PreferencesOfSAMMOption
							.getPreferenceSaveImageHorizontalCrop(mContext));
			canvasOption.mSAMMOption
					.setSaveImageRightCroppingOption(PreferencesOfSAMMOption
							.getPreferenceSaveImageHorizontalCrop(mContext));
			canvasOption.mSAMMOption
					.setSaveImageTopCroppingOption(PreferencesOfSAMMOption
							.getPreferenceSaveImageVerticalCrop(mContext));
			canvasOption.mSAMMOption
					.setSaveImageBottomCroppingOption(PreferencesOfSAMMOption
							.getPreferenceSaveImageVerticalCrop(mContext));
			canvasOption.mSAMMOption
					.setSaveContentsCroppingOption(PreferencesOfSAMMOption
							.getPreferenceSaveContentsCrop(mContext));
			// medium size : to reduce saving time
			canvasOption.mSAMMOption.setSaveImageSize(PreferencesOfSAMMOption
					.getPreferenceSaveImageSize(mContext));
			// canvasOption.mSAMMOption.setSaveImageSize(SOptionSAMM.SAMM_SAVE_OPTION_MEDIUM_SIZE);
			// valid only to save jpg
			// canvasOption.mSAMMOption.setJPGImageQuality(100);
			// content quality minimum
			canvasOption.mSAMMOption.setContentsQuality(PreferencesOfSAMMOption
					.getPreferenceSaveImageQuality(mContext));
			// canvasOption.mSAMMOption.setContentsQuality(SOptionSAMM.SAMM_CONTENTS_QUALITY_MINIMUM);
			// save with background setting
			canvasOption.mSAMMOption
					.setSaveOnlyForegroundImage(false); 
			
			canvasOption.mSAMMOption
					.setCreateNewImageFile(PreferencesOfSAMMOption
							.getPreferenceSaveCreateNewImageFile(mContext)); 
			canvasOption.mSAMMOption
					.setEncodeForegroundImage(PreferencesOfSAMMOption
							.getPreferenceEncodeForegroundImageFile(mContext));
			canvasOption.mSAMMOption
					.setEncodeThumbnailImage(PreferencesOfSAMMOption
							.getPreferenceEncodeThumbnailImageFile(mContext));
			canvasOption.mSAMMOption
					.setEncodeObjectData(PreferencesOfSAMMOption
							.getPreferenceEncodeObjectDataFile(mContext));
			canvasOption.mSAMMOption
					.setDecodePriorityFGData(PreferencesOfSAMMOption
							.getPreferenceDecodePriorityFGData(mContext));
			canvasOption.mSAMMOption
					.setEncodeVideoFileDataOption(PreferencesOfSAMMOption
							.getPreferenceEncodeVideoFileData(mContext));
			// option setting
			mSCanvas.setOption(canvasOption);
			saveSAMMFile(saveFileName, true);
		}
	}

	private void showObjectPopUpMenu(int nEventPositionX, int nEventPositionY) {

		int nSelectObjectType = mSCanvas.getSelectedSObjectType();
		final boolean bClipboardObjectExist = mSCanvas
				.isClipboardSObjectListExist();
		int nMenuArray;
		final int xPos = nEventPositionX;
		final int yPos = nEventPositionY;

		if (nSelectObjectType == SObject.SOBJECT_LIST_TYPE_IMAGE) {

			if (bClipboardObjectExist)
				nMenuArray = R.array.popup_menu_image_with_paste;
			else
				nMenuArray = R.array.popup_menu_image;

			new AlertDialog.Builder(this)
					.setTitle("Select Image Menu")
					.setItems(nMenuArray,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// Delete Image
									if (which == 0) {
										deleteSelectedSObject();
									}
									// Rotate Image
									else if (which == 1) {
										// rotateSelectedObject();
									}
									// Copy Image
									else if (which == 2) {
										copySelectedObject();
									}
									// Cut Image
									else if (which == 3) {
										cutSelectedObject();
									}
									// Paste object in clipboard
									else if (which == 4) {
										pasteClipboardObject(xPos, yPos);
									}
									// Clear object in clipboard
									else if (which == 5) {
										clearClipboardObject();
									}
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		} else if (nSelectObjectType == SObject.SOBJECT_LIST_TYPE_TEXT) {

			if (bClipboardObjectExist)
				nMenuArray = R.array.popup_menu_text_with_paste;
			else
				nMenuArray = R.array.popup_menu_text;

			new AlertDialog.Builder(this)
					.setTitle("Select Text Menu")
					.setItems(nMenuArray,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// Delete Text
									if (which == 0) {
										deleteSelectedSObject();
									}
									// Copy Text
									else if (which == 1) {
										copySelectedObject();
									}
									// Cut Text
									else if (which == 2) {
										cutSelectedObject();
									}
									// Paste object in clipboard
									else if (which == 3) {
										pasteClipboardObject(xPos, yPos);
									}
									// Clear object in clipboard
									else if (which == 4) {
										clearClipboardObject();
									}
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		} else {

			if (bClipboardObjectExist) {
				nMenuArray = R.array.popup_menu_else_with_paste;

				new AlertDialog.Builder(this)
						.setTitle("Select Pop-Up Menu")
						.setItems(nMenuArray,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// Paste object in clipboard
										if (which == 0) {
											pasteClipboardObject(xPos, yPos);
										}
										// Clear object in clipboard
										else if (which == 1) {
											clearClipboardObject();
										}
									}
								})
						.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			}
		}
	}


	public String loadSAMMDataString(Note paramnote) {
		String path;
		path = getFilesDir().getAbsolutePath() + "/" + paramnote.getYear()
				+ paramnote.getMonth() + paramnote.getDate() + index + ".png";
		return path;
	}

	// init note var from loading the xml file
	protected Note loadNoteFromFile(Note paramnote) {
		Note load_note;
		load_note = NoteControler.getXmlStringDate(dirPath,
				paramnote.getYear(), paramnote.getMonth(), paramnote.getDate(),
				this);
		return load_note;
	}

	protected void createNoteFile(File file) {
		// TODO Auto-generated method stub

		try {
			file.createNewFile();
			// ////
			FileOutputStream fos = openFileOutput(notename,
					Context.MODE_PRIVATE);
			fos.write(NoteControler.makeNoteXml(note, mSCanvas, this)
					.toString().getBytes());

			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void delete_cur_file() {

		file.delete();

	}

	private void delete_xml_dropbox() {
		dropbox_controler.delete_xml(notename, note);
	}

	private void upload_to_dropbox() {
		dropbox_controler.upload_xml(notename);
		dropbox_controler.upload_png(note);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (data == null)
				return;
			if (requestCode == REQUEST_CODE_INSERT_VIDEO_OBJECT) {
				// Temporary
				String strVideoFile = data.getStringExtra("videofilename");
				if (addVideoFileObject(strVideoFile)) {
					Toast.makeText(this,
							"Insert video file(" + strVideoFile + ") Success!",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(this,
							"Insert video file(" + strVideoFile + ") Fail!",
							Toast.LENGTH_LONG).show();
				}
			} else if (requestCode == REQUEST_CODE_SELECT_IMAGE_BACKGROUND) {
				Uri imageFileUri = data.getData();
				if (imageFileUri == null)
					return;
				String strBackgroundImagePath = SPenSDKUtils
						.getRealPathFromURI(this, imageFileUri);

				// Check Valid Image File
				if (!SPenSDKUtils.isValidImagePath(strBackgroundImagePath)) {
					Toast.makeText(this, "Invalid image path or web image",
							Toast.LENGTH_LONG).show();
					return;
				}

				// Set SCanvas
				if (!mSCanvas.setBGImagePath(strBackgroundImagePath)) {
					Toast.makeText(mContext,
							"Fail to set Background Image Path.",
							Toast.LENGTH_LONG).show();
				}
			}

			switch (requestCode) {
			case CROP_FROM_CAMERA:
				final Bundle extras = data.getExtras();

				if (extras != null) {
					Bitmap photo = extras.getParcelable("data");
					// ////////////////////image insert
					SOptionSCanvas canvasOption = mSCanvas.getOption();
					if (canvasOption == null)
						return;

					if (canvasOption.mSAMMOption == null)
						return;

					canvasOption.mSAMMOption
							.setContentsQuality(PreferencesOfSAMMOption
									.getPreferenceSaveImageQuality(mContext));
					// option setting
					mSCanvas.setOption(canvasOption);

					// the pen stroke creation
					int nContentsQualityOption = canvasOption.mSAMMOption
							.getContentsQuality();
					SObjectImage sImageObject = new SObjectImage(
							nContentsQualityOption);

					// Set rect
					RectF rectF = getDefaultRect(SPenSDKUtils
							.getRealPathFromURI(this, mImageCaptureUri));
					sImageObject.setRect(rectF);
					// Set style : normal
					sImageObject.setStyle(SObjectImage.SAMM_IMAGESTYLE_NORMAL);
					// Set image bitmap
					sImageObject.setImagePath(SPenSDKUtils.getRealPathFromURI(
							this, mImageCaptureUri));

					// not selected
					mSCanvas.insertSAMMImage(sImageObject, false);
				}

				File f = new File(mImageCaptureUri.getPath());
				if (f.exists()) {
					f.delete();
				}
				break;
			case PICK_FROM_ALBUM:
				Log.d("???????", "???????");
				mImageCaptureUri = data.getData();
			case PICK_FROM_CAMERA:
				
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(mImageCaptureUri, "image/*");
				intent.putExtra("scale", true);
				// intent.putExtra("return-data", true);
				intent.putExtra("output", mImageCaptureUri);
				startActivityForResult(intent, CROP_FROM_CAMERA);
				break;
			default:
				break;
			}
		}
	}

	private void callGalleryForInputImage(int nRequestCode) {
		doTakeAlbumAction();
	}

	private void applyOtherOption() {
		int hoverPointerShowOption = PreferencesOfOtherOption
				.getPreferenceHoverPointerShowOption(mContext);
		switch (hoverPointerShowOption) {
		case HOVER_SHOW_ALWAYS_ONHOVER:
			mSCanvas.setSCanvasHoverPointerShowOption(SCanvasConstants.SCANVAS_HOVERPOINTER_SHOW_OPTION_ALWAYS_ON_HOVER);
			break;
		case HOVER_SHOW_ONCE_ONHOVER:
			mSCanvas.setSCanvasHoverPointerShowOption(SCanvasConstants.SCANVAS_HOVERPOINTER_SHOW_OPTION_ONCE_ON_HOVER);
			break;
		}

		int hoverPointerStyle = PreferencesOfOtherOption
				.getPreferenceHoverPointerStyle(mContext);
		switch (hoverPointerStyle) {
		case HOVER_POINTER_DEFAULT:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_NONE);
			break;
		case HOVER_POINTER_SIMPLE_ICON:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SIMPLE_CUSTOM);
			mSCanvas.setSCanvasHoverPointerSimpleIcon(SPenEventLibrary.HOVERING_SPENICON_MOVE);
			break;
		case HOVER_POINTER_SIMPLE_DRAWABLE:
			// mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SIMPLE_CUSTOM);
			// mSCanvas.setSCanvasHoverPointerSimpleDrawable(getResources()
			// .getDrawable(R.drawable.tool_ic_pen));
			break;
		case HOVER_POINTER_SPEN:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SPENSDK);
			break;
		case HOVER_POINTER_SNOTE:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SNOTE);
			break;
		}

		mSideButtonStyle = PreferencesOfOtherOption
				.getPreferencePenSideButtonStyle(mContext);
		mSCanvas.setKeyboardPredictiveTextDisable(!PreferencesOfOtherOption
				.getPreferencePredictiveText(mContext));
		mSCanvas.setSettingViewPinUpState(PreferencesOfOtherOption
				.getPreferenceSettingviewPinup(mContext));
		// mSCanvas.setFingerControlPenDrawing(PreferencesOfOtherOption.getPreferencePenOnlyMode(mContext));
		mSCanvas.setFingerControlPenDrawing(false);
		mSCanvas.setStrokeLongClickSelectOption(PreferencesOfOtherOption
				.getPreferenceStrokeLongclick(mContext));
		mSCanvas.setTextLongClickSelectOption(PreferencesOfOtherOption
				.getPreferenceTextLongclick(mContext));
		mSCanvas.setEnableHoverScroll(PreferencesOfOtherOption
				.getPreferenceHoverScroll(mContext));
		mSCanvas.maintainScaleOnResize(PreferencesOfOtherOption
				.getPreferenceMaintainScaleOnResize(mContext));
		mSCanvas.maintainSettingPenColor(PreferencesOfOtherOption
				.getPreferenceMaintainPenColor(mContext));
		mSCanvas.supportBeautifyStrokeSetting(PreferencesOfOtherOption
				.getPreferenceSupportBeautifyStrokeSetting(mContext));
		mSCanvas.setEnableBoundaryTouchScroll(PreferencesOfOtherOption
				.getPreferenceBoundaryTouchScroll(mContext));
	}

	// Side Button during hover
	private void doHoverButtonUp(int nEventPositionX, int nEventPositionY) {

		// --------------------------------------------------------------
		// Close setting view if the setting view is visible
		// --------------------------------------------------------------
		if (mSCanvas
				.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN)) {
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN,
					false);
			return;
		} else if (mSCanvas
				.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER)) {
			mSCanvas.showSettingView(
					SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
			return;
		} else if (mSCanvas
				.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT)) {
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT,
					false);
			return;
		} else if (mSCanvas
				.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING)) {
			mSCanvas.showSettingView(
					SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, false);
			return;
		}

		// --------------------------------------------------------------
		// Show popup menu if the object is selected or if sobject exist in
		// clipboard
		// --------------------------------------------------------------
		if (mSCanvas.isSObjectSelected()
				|| mSCanvas.isClipboardSObjectListExist()) {
			showObjectPopUpMenu(nEventPositionX, nEventPositionY);
			return;
		}

		// --------------------------------------------------------------
		// Show Setting view
		// --------------------------------------------------------------
		if (mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_PEN) {
			mSCanvas.setSettingViewSizeOption(
					SCanvasConstants.SCANVAS_SETTINGVIEW_PEN,
					SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN,
					true);
		} else if (mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER) {
			mSCanvas.setSettingViewSizeOption(
					SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER,
					SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
			mSCanvas.showSettingView(
					SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, true);
		} else if (mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_TEXT) {
			mSCanvas.setSettingViewSizeOption(
					SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT,
					SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT,
					true);
		} else if (mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_FILLING) {
			mSCanvas.setSettingViewSizeOption(
					SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING,
					SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
			mSCanvas.showSettingView(
					SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, true);
		}
	}

	public void parser() {

		try {
			URL url = new URL(
					"http://www.kma.go.kr/XML/weather/sfc_web_map.xml");
			XmlPullParserFactory parserFactory = XmlPullParserFactory
					.newInstance();
			XmlPullParser parser = parserFactory.newPullParser();
			parser.setInput(url.openStream(), "utf-8");

			int parserEvent = parser.getEventType();
			String tag = "ready..!";

			String stnid = "";
			String desc = "";
			String ta = "";
			String region = "";

			while (parserEvent != XmlPullParser.END_DOCUMENT) {

				switch (parserEvent) {
				case XmlPullParser.START_DOCUMENT:
					Log.d("parserTest", "Parser Start..!");
					break;
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					if (tag.equals("local")) {
						stnid = parser.getAttributeValue(null, "stn_id");
						desc = parser.getAttributeValue(null, "desc");
						ta = parser.getAttributeValue(null, "ta");
					}
					break;
				case XmlPullParser.TEXT:
					if (tag.equals("local")) {
						region = parser.getText();
					}
					break;

				case XmlPullParser.END_TAG:
					if (tag.equals("local")) {
						if (region.equals(selectedregion))
							setWeatherImage(region, desc, ta);
					}
					tag = "nothing";
					break;
				}
				parserEvent = parser.next();
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.d("parserTest", "error");
		}
	}

	public void setWeatherImage(String region, String desc, String ta) {

		tempTextView.setText(ta);

		Drawable weatherImage = null;

		if (desc.equals("맑음")) {
			weatherImage = getResources().getDrawable(R.drawable.w_sunny);
		} else if (desc.equals("비")) {
			weatherImage = getResources().getDrawable(R.drawable.w_rain);
		} else if (desc.equals("구름조금")) {
			weatherImage = getResources()
					.getDrawable(R.drawable.w_cloud_little);
		} else if (desc.equals("구름많이")) {
			weatherImage = getResources().getDrawable(R.drawable.w_cloud_much);
		} else if (desc.equals("흐림")) {
			weatherImage = getResources().getDrawable(R.drawable.w_cloud);
		} else if (desc.equals("박무")) {
			weatherImage = getResources().getDrawable(R.drawable.w_thin_mist);
		} else if (desc.equals("안개")) {
			weatherImage = getResources().getDrawable(R.drawable.w_mist);
		} else if (desc.equals("연무")) {
			weatherImage = getResources().getDrawable(R.drawable.w_smog);
		} else if (desc.equals("황사")) {
			weatherImage = getResources().getDrawable(R.drawable.w_yellow_dust);
		} else if (desc.equals("천둥번개")) {
			weatherImage = getResources().getDrawable(R.drawable.w_thunder);
		} else if (desc.equals("눈")) {
			weatherImage = getResources().getDrawable(R.drawable.w_snow);
		} else if (desc.equals("소나기")) {
			weatherImage = getResources().getDrawable(R.drawable.w_shower);
		} else if (desc.equals("비 또는 눈")) {
			weatherImage = getResources().getDrawable(R.drawable.w_rainorsnow);
		} else if (desc.equals("-")) {
			weatherImage = getResources().getDrawable(R.drawable.w_sunny);
		} else if (desc.equals("눈 또는 비")) {
			weatherImage = getResources().getDrawable(R.drawable.w_snoworrain);
		}
		conditionImage.setImageDrawable(weatherImage);
		if (isNew) {
			note.getNoteDiaries().get(index).setTemperature(ta);
			note.getNoteDiaries().get(index).setWeatherImg(desc);
		}
	}


	private void doTakeAlbumAction() {
		// ?��? ?��?
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent, PICK_FROM_ALBUM);
	}

	private CustomAdapter setAdapter() {
		Vector<CData> vector = new Vector<CData>();
		// SAPI sapi = new SAPI();
		sapi.setFacebookAccessToken(session);
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getText() != null) {
				CData cData;
				if (list.get(i).getFeedImg() != null) {
					cData = new CData(sapi.getUser("facebook", list.get(i).getUserId()).getName(),list.get(i).getText(),downloadBitmap(sapi.getUser("facebook",
							list.get(i).getUserId()).getUserProfileUrl()),downloadBitmap(list.get(i).getFeedImg()),list.get(i).getFeedDate().replace("T", " ").replace("+0000", " "),list.get(i).getFeedId());
				} else {
					cData = new CData(sapi.getUser("facebook", list.get(i).getUserId()).getName(),list.get(i).getText(),downloadBitmap(sapi.getUser("facebook",
							list.get(i).getUserId()).getUserProfileUrl()),null,list.get(i).getFeedDate().replace("T", " ").replace("+0000", " "),list.get(i).getFeedId());
				}
				vector.add(cData);
			}
		}
		return new CustomAdapter(this, R.layout.myitem, vector, this);
	}

	public Bitmap downloadBitmap(String urlname) {
		Log.d("URl", "url " + urlname);
		URL url = null;
		Bitmap Result = null;
		try {
			url = new URL(urlname);
			Result = BitmapFactory.decodeStream(url.openStream());
			Bitmap.createScaledBitmap(Result, 10, 10, false);
			Log.d("URL", "herecome?" + Result);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Result = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Result = null;
		}
		return Result;
	}

	private Session createSession() {
		Session activeSession = Session.getActiveSession();
		if (activeSession == null || activeSession.getState().isClosed()) {
			activeSession = new Session.Builder(this).setApplicationId(
					PreferencesOfOtherOption.applicationId).build();
			Session.setActiveSession(activeSession);
		}
		return activeSession;
	}

	public Handler mCompleteHandler = new Handler() {
		public void handleMessage(Message msg) {
			bDownloading = false;

			mProgCircle.setVisibility(View.INVISIBLE);
			feedListView.setAdapter(customfeedlistAdapter);
		}
	};

	// ////subject choose dialog
	public class CustomDialog extends Dialog implements
			android.view.View.OnClickListener {
		// ImageView
		// e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,e11,e12,e13,e14,e15,e16,e17,e18,e19,e20,e21,e22,e23,e24;
		ImageView[] eArr = new ImageView[24];
		Button btn_cancel;
		SubjectSelect subjectSelect;
		int[] idlist = new int[24];

		public CustomDialog(Context context) {
			super(context);
			setContentView(R.layout.subject_choose_dialog);
			this.setTitle("Select Subject!");
			subjectSelect = new SubjectSelect();
			btn_cancel = (Button) findViewById(R.id.btn_sub_choose_cancel);
			btn_cancel.setOnClickListener(this);
			idlist[0] = R.id.sb_e1;
			idlist[1] = R.id.sb_e2;
			idlist[2] = R.id.sb_e3;
			idlist[3] = R.id.sb_e4;
			idlist[4] = R.id.sb_e5;
			idlist[5] = R.id.sb_e6;
			idlist[6] = R.id.sb_e7;
			idlist[7] = R.id.sb_e8;
			idlist[8] = R.id.sb_e9;
			idlist[9] = R.id.sb_e10;
			idlist[10] = R.id.sb_e11;
			idlist[11] = R.id.sb_e12;
			idlist[12] = R.id.sb_e13;
			idlist[13] = R.id.sb_e14;
			idlist[14] = R.id.sb_e15;
			idlist[15] = R.id.sb_e16;
			idlist[16] = R.id.sb_e17;
			idlist[17] = R.id.sb_e18;
			idlist[18] = R.id.sb_e19;
			idlist[19] = R.id.sb_e20;
			idlist[20] = R.id.sb_e21;
			idlist[21] = R.id.sb_e22;
			idlist[22] = R.id.sb_e23;
			idlist[23] = R.id.sb_e24;
			for (int i = 0; i < 24; i++) {
				eArr[i] = (ImageView) findViewById(idlist[i]);
				eArr[i].setOnClickListener(this);
			}
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			for (int i = 0; i < 24; i++) {
				if (v == eArr[i]) {
					Log.d("subject select", String.valueOf(i));
					iv_diary_subject.setImageBitmap(BitmapFactory
							.decodeResource(getResources(),
									subjectSelect.getDrawable(i + 1)));
					note.getNoteDiaries().get(index).setSubject(i + 1);
					dismiss();
				}
			}
			if (v == btn_cancel) {
				dismiss();
			}
		}
	}

	private void recordStart() {

		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
		recorder = new MediaRecorder();

		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		recorder.setOutputFile(dirPath + "/record/" + recordfilename);

		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			Toast.makeText(getApplicationContext(),
					"error : " + e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Toast.makeText(getApplicationContext(), "녹음 중", Toast.LENGTH_LONG)
				.show();
	}

	private void recordStop() {

		if (recorder == null) {
			return;
		}

		recorder.stop();
		recorder.release();
		recorder = null;

		Toast.makeText(getApplicationContext(), "녹음 중지", Toast.LENGTH_LONG)
				.show();
		note.getNoteDiaries().get(index).setRecordFileName(recordfilename);
	}

	private void playStart() {

		if (player != null) {
			player.stop();
			player.release();
			player = null;
		}

		try {
			player = new MediaPlayer();
			FileInputStream fs = new FileInputStream(dirPath + "/record/"
					+ recordfilename);
			FileDescriptor fd = fs.getFD();
			player.setDataSource(fd);
			player.prepare();
			player.start();
			fs.close();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Toast.makeText(getApplicationContext(), "재생 시작", Toast.LENGTH_LONG)
				.show();

	}

	private void deleteRecord() {
		File file = new File(dirPath + "/record", recordfilename);
		if (file.exists()) {
			file.delete();
		}else{
			Toast.makeText(this, "삭제할 파일이 없습니다.", Toast.LENGTH_SHORT).show();
		}
		recordfilename = "null";
		note.getNoteDiaries().get(index).setRecordFileName(recordfilename);
	}
}
