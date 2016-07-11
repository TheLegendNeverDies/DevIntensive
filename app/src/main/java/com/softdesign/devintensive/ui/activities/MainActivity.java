package com.softdesign.devintensive.ui.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    private DataManager mDataManager;

    private int mCurrentEditMode = 0;

    private ImageView mCallImg;
    private ImageView mOpenGitImg;
    private ImageView mSendMailImg;
    private ImageView mOpenVkImg;
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDrawer;
    private FloatingActionButton mFab;
    private EditText mUserPhone, mUserMail, mUserVk, mUserGit, mUserBio;
    private List<EditText> mUserInfoViews;
    private RelativeLayout mProfilePlaceholder;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private AppBarLayout.LayoutParams mAppbarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;
    private AppBarLayout mAppBarLayout;
    private ImageView mProfileImage;

    /**
     * Метод вызывается при создании активити после изменения конфигурации/возврата к текушей
     * активити после его уничтожения.
     *
     * в данном методе инициализируются/производится:
     * - UI пользовательский интерфейс (статика)
     * - инициализация статических данных активити
     * - связь данных со списками (инициализация адаптеров)
     *
     * Не запускать длительные операции по работе с данными в onCreate() !!!
     *
     * @param savedInstanceState - объект со значениями сохраненными в Bundle - состояние UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        mSendMailImg = (ImageView) findViewById(R.id.send_img);
        mOpenGitImg = (ImageView) findViewById(R.id.open_git_img);
        mOpenVkImg = (ImageView) findViewById(R.id.open_vk_img);

        mDataManager = DataManager.getInstance();
        mCallImg = (ImageView) findViewById(R.id.call_img);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_conteiner);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mAppBarLayout  = (AppBarLayout) findViewById(R.id.appbar_layout);
        mUserPhone = (EditText) findViewById(R.id.editPhone);
        mUserMail = (EditText) findViewById(R.id.editEmail);
        mUserVk = (EditText) findViewById(R.id.editVk);
        mUserGit = (EditText) findViewById(R.id.editGit);
        mUserBio = (EditText) findViewById(R.id.editProfile);
        mProfilePlaceholder= (RelativeLayout) findViewById(R.id.profile_placeholder);
        mProfileImage = (ImageView) findViewById(R.id.user_photo_img);

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserMail);
        mUserInfoViews.add(mUserVk);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserBio);



        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);
        mCallImg.setOnClickListener(this);
        mSendMailImg.setOnClickListener(this);
        mOpenGitImg.setOnClickListener(this);
        mOpenVkImg.setOnClickListener(this);



        setupToolbar();
        setupDrawer();
        loadUserInfoValue();


        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.user_photo)
                .into(mProfileImage);

        if (savedInstanceState == null){
            //активити запускается впервые

        } else {
            //активити уже создавалось
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Метод вызывается при старте активити перед моментом того как UI станет достепен пользователю
     * как правило в данном методе происходит регистрация подписки на событиея остановка которых была
     * произведена в onStop()
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    /**
     * Метод вызывается когда активити становится доступен пользователю для взаимодействия
     * в данном методе как правило происходит запуск анимаций/аудио/видео/запуск BroadcastReceiver
     * необходимых для реализации UI логика/запуск выполнение потоков и т.д.
     * метод должен быть максимально легковесным для максимальной отзывчивости UI
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    /**
     * Метод вызывается когда текущая активити теряет фокус но остается видимой(всплытие
     * диалогового окна/частичное перекрытие другой активити и т.д)
     * <p/>
     * в данном методе реализуют сохранение легковесных UI данных/анимаций/аудио/видео  и т.д
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        saveUserInfoValue();
    }

    /**
     * Метод вызывается когда активити становится невидимым для пользователя.
     * в данном методе происходит отписка от событий, остановка сложных анимаций, сложные операции по
     * сохранению данных/прерывание запущенных потоков и т.д.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    /**
     * Метод вызывается при окончании работы активити (когда это происходит системно или после вызова
     * метода finish())
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    /**
     * Метод вызывается при рестарте активити/возобнавлении работы после вызова метода onStop()
     * в данном методе реализуется спецификация бизнес логики которая должна быть реализована именно
     * при рестарте активности - например запрос к серверу который необходимо вызвать при возвращении
     * из другой активности (обновление данных, подписка на определенное событие проинициализированное
     * на другом экране/специфическая бизнес логика завязанная именно на перезапуске активити)
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                if (mCurrentEditMode == 0){
                    changeEditMode(1);
                    mCurrentEditMode = 1;
                } else {
                    changeEditMode(0);
                    mCurrentEditMode = 0;
                }
                break;
            case R.id.profile_placeholder:
                //  TODO: 07.07.2016 сделать выбор откуда загрузить фото
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;

            case R.id.call_img:
                if (mCurrentEditMode == 0) {
                    callPhone(mUserPhone.getText().toString());
                }
                break;
            case R.id.send_img:
                if (mCurrentEditMode == 0) {
                    sendMail(mUserMail.getText().toString());
                }
                break;
            case R.id.open_git_img:
                if (mCurrentEditMode == 0) {
                    browseUrl("https://" + mUserGit.getText().toString());
                }
                break;
            case R.id.open_vk_img:
                if (mCurrentEditMode == 0) {
                    browseUrl("https://" + mUserVk.getText().toString());
                }
                break;

        }
    }
    private void callPhone(String phoneStr) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneStr));
        startActivity(dialIntent);
    }
    private void sendMail(String email) {
        Intent mailIntent = new Intent(Intent.ACTION_SEND);
        mailIntent.setType("message/rfc822");
        String  mail = Uri.parse(email).toString();
        String[] sendmail = {mail};
        mailIntent.putExtra(Intent.EXTRA_EMAIL  , sendmail);
        startActivity(Intent.createChooser(mailIntent, "Send Email"));
        try {
            startActivity(Intent.createChooser(mailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            showToast("There are no email clients installed.");
        }
    }

    private void browseUrl(String url) {
        Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browseIntent);
    }
    /**
     * Получение результата из другой Activity (фото из камеры или галлереи)
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null){
                    mSelectedImage = data.getData();
                    Log.d(TAG, "REQUEST_GALLERY_PICTURE");
                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null){
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                    Log.d(TAG, "REQUEST_CAMERA_PICTURE");
                    insertProfileImage(mSelectedImage);
                }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    private void showSnackbar(String message){
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void setupToolbar(){
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        mAppbarParams = (AppBarLayout.LayoutParams)mCollapsingToolbar.getLayoutParams();
        if (actionBar != null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_dehaze_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackbar(item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }



    /**
     * переключает режим редактирования
     * @param mode если 1 режим редактирования, если 0 режим просмотра
     */


    private void changeEditMode(int mode){
        if (mode == 1) {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {


                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
                showProfileplaceholder();
                lockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
            }
        } else {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {


                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
                saveUserInfoValue();
                hideProfilePaceholder();
                unlockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
            }
        }
    }

    private void loadUserInfoValue(){
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    private void saveUserInfoValue(){
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            // Take care of calling this method on earlier versions of
            // the platform where it doesn't exist.
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
        // This will be called either automatically for you on 2.0
        // or later, or by the code above on earlier versions of the
        // platform.
        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START)){
            mNavigationDrawer.closeDrawer(GravityCompat.START);
            Log.d(TAG, "Drawer opened");
        } else {
            Log.d(TAG, "Drawer closed");
            super.onBackPressed();
        };
    }

    private void loadPhotoFromGallery(){
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent,getString(R.string.user_profile_chose_message)), ConstantManager.REQUEST_GALLERY_PICTURE);
    }

    private void loadPhotoFromCamera(){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE )== PackageManager.PERMISSION_GRANTED) {

            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: 07.07.2016 обработать ошибку
            }

            if (mPhotoFile != null) {
                // TODO: 07.07.2016 передать фотофайл в интент
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

            Snackbar.make(mCoordinatorLayout, "Для корректной работы приложения необходимо дать требуемые разрешения", Snackbar.LENGTH_LONG)
                    .setAction("Разрешить", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }
    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }



    public void openApplicationSettings(){
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));

        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // TODO: 08.07.2016 тут обрабатываем разрешение (разрешение получено)
            }

            if (grantResults[1] == PackageManager.PERMISSION_GRANTED){
                // TODO: 08.07.2016 тут обрабатываем разрешение (разрешение получено)
            }
        }


    }


    private void hideProfilePaceholder(){

        mProfilePlaceholder.setVisibility(View.GONE);

    }

    private void showProfileplaceholder(){

        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }
    private  void lockToolbar(){
        mAppBarLayout.setExpanded(true,true);
        mAppbarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppbarParams);

    }
    private  void unlockToolbar(){
        mAppBarLayout.setExpanded(false);
        mAppbarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL| AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppbarParams);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {getString(R.string.user_profile_dialog_gallery),
                        getString(R.string.user_profile_dialog_photo),
                        getString(R.string.user_profile_dialog_cancel)};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.user_profile_dialog_title);
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int choiceItem) {
                        switch (choiceItem){
                            case 0:
                                // TODO: 07.07.2016 загрузить из галереи
                                loadPhotoFromGallery();
                                showSnackbar("загрузить из галереи");
                                break;
                            case 1:
                                // TODO: 07.07.2016 загрузить из камеры
                                loadPhotoFromCamera();
                                showSnackbar("загрузить из камеры");
                                break;
                            case 2:
                                dialogInterface.cancel();
                                // TODO: 07.07.2016 отмена
                                showSnackbar("отмена");
                                break;
                        }
                    }
                });
                return builder.create();
            default:
                showSnackbar("отмена");
                return null;
        }
    }
    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .into(mProfileImage);

        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
    }


}
