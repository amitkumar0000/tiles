package com.lpirro.tiledemo.customquicksettings.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.admin.DevicePolicyManager
import android.content.*
import android.database.ContentObserver
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.*
import com.androidbolts.topsheet.TopSheetBehavior
import com.lpirro.tiledemo.*
import com.lpirro.tiledemo.customquicksettings.*
import com.lpirro.tiledemo.databinding.ActivityCustomQuikSettingBinding
import com.lpirro.tiledemo.databinding.TextInpuPasswordBinding
import com.lpirro.tiledemo.sharing.ExitQSettingReceiver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.DataOutputStream
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class QuickSettingService : Service() {

    private var binding: ActivityCustomQuikSettingBinding? = null
    private val windowManager by lazy {  getSystemService(WINDOW_SERVICE) as WindowManager }

    private val alertbinding by lazy {  TextInpuPasswordBinding.inflate(LayoutInflater.from(this)) }

    private val exitReceiver by lazy { ExitQSettingReceiver() }

    private lateinit var topSheetBehavior: TopSheetBehavior<View>


    private val disposable = CompositeDisposable()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    val mDevicePolicyManager: DevicePolicyManager by lazy {  getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager }
    val  mComponentName: ComponentName by lazy { ComponentName(this, DeviceAdminDemo::class.java) }

    private val tilesAdapter by lazy { TilesAdapter(this, windowManager) }
    val sharedpreferences by lazy {  getSharedPreferences("MyPREFERENCES", MODE_PRIVATE) }


    private val notificationAdapter by lazy { NotificationAdapter{
        binding?.clearNotText?.isVisible = it
    } }

    private  lateinit var observer: ContentObserver


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        listenToBus()
        observeNotification()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            attachForegroundNotification()
            init()
            Log.d("STATUS", " attachForegroundNotification complete received")

        }

        registerReceiver(exitReceiver, IntentFilter().apply {
            addAction("android.intent.action.exit.qsetting")
        })

        startCollapseExpand()


        return START_STICKY
    }

    private fun startCollapseExpand() {

        topSheetBehavior.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float, isOpening: Boolean?) {
                Log.d("Top Sheet ", "$slideOffset     $isOpening")
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Log.d("STATUS ", " $newState")
                when (newState) {
                    4 -> {
                        binding?.topCoordinator?.alpha = 0.0f
                    }
                    else -> {
                        binding?.topCoordinator?.alpha = 1.0f
                    }
                }
            }
        })

        binding?.topSheet?.setOnDragListener { v, event ->
            when(event) {

            }
           true
        }
    }

    fun listenToBus() {
        RxBus.listen()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
            when(it) {
                is CloseQuickSetting -> {
                    closeQuickSettingMenu()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun attachForegroundNotification() {
        val NOTIFICATION_CHANNEL_ID = "com.lpirro.tiledemo"
        val channelName = "My Background Service"
        val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan);


        val notificationIntent = Intent(this, QuickSettingService::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0)
// 1
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("QuickSetting")
                .setContentText("QuickSetting is running")
                .setSmallIcon(R.drawable.ic_wifi)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(123, notification)
    }


    private fun init() {
        if(binding == null) {
            binding = ActivityCustomQuikSettingBinding.inflate(LayoutInflater.from(this))
            topSheetBehavior = TopSheetBehavior.from(binding!!.topSheet)

            initQuickSettingTiles()
            initNotification()
            showQuickSettingMenu()

            stopQuickSetting()
            observeNotification()
        }
    }

    private fun stopQuickSetting() {
//        binding!!.menuOption.setOnClickListener {
//
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                mDevicePolicyManager.setStatusBarDisabled(mComponentName, false)
//            }
//            stopForeground(true)
//            stopSelf()
//
//        }
    }

    private fun observeNotification() {
        RxBus.listenNotification()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("Amit ", " Received notification $it")
                    notificationAdapter.setData(it)
                }, {

                })
    }

    private fun initQuickSettingTiles() {
        binding?.customQuickSetting?.adapter = tilesAdapter

        val layoutManager = GridLayoutManager(this, 4)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when(tilesAdapter.getViewType(position)) {
                    TILES -> {
                        1
                    }
                    BRIGHTNESS -> {
                        4
                    }
                    NOTIFICATIOn -> {
                        4
                    }
                    else -> {
                        throw IllegalArgumentException("View holder of this position not supported")
                    }
                }
            }
        }

        binding?.customQuickSetting?.layoutManager = layoutManager
//        binding?.customQuickSetting?.suppressLayout(true)

        tilesAdapter.setData(
                listOf(
                        QuickSettingModel.TilesModel(WIFI, R.drawable.ic_wifi),
                        QuickSettingModel.TilesModel(BLUETOOTH, R.drawable.ic_bluetooth),
                        QuickSettingModel.TilesModel(GPS, R.drawable.ic_gps_off),

                        QuickSettingModel.TilesModel(FLASHLIGHT, R.drawable.ic_flashlight),
                        QuickSettingModel.TilesModel(NFC, R.drawable.ic_nfc),

                        QuickSettingModel.TilesModel(MOBILEDATA, R.drawable.ic_cell_wifi),
//                        QuickSettingModel.BrightnessModel,
//                        QuickSettingModel.NotificationModel("Bluetooth", "Switch on Bluetooth", R.drawable.wifi_on_state)
                )
        )

        binding?.customQuickSetting?.isVerticalScrollBarEnabled = false


    }

    private fun initNotification() {
        binding?.notificationList?.adapter = notificationAdapter

        binding?.notificationList?.layoutManager = LinearLayoutManager(this)

        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter = notificationAdapter))
        itemTouchHelper.attachToRecyclerView(binding?.notificationList)

        binding?.clearNotText?.setOnClickListener {
            notificationAdapter.deleteAll()
            RxBus.publish(ClearAllNotification)

        }

//        if (Settings.Secure.getString(this.contentResolver, "enabled_notification_listeners").contains(applicationContext.getPackageName())) {
//            //Add the code to launch the NotificationService Listener here.
//            startService(Intent(this, CustomStatusBarNotification::class.java))
//        } else {
//            //Launch notification access in the settings...
//            applicationContext.startActivity( Intent(
//                    "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").apply {
//                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            });
//        }

        Utils.grantNotificationAccess()

        observeNotification()
    }

    private fun showQuickSettingMenu() {

        if(binding!!.root.isShown)
            return

        val localLayoutParams = WindowManager.LayoutParams()

        localLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        localLayoutParams.gravity = Gravity.TOP
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL

        localLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        localLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        localLayoutParams.format = PixelFormat.TRANSPARENT

        windowManager.addView(binding!!.root, localLayoutParams)

        binding?.quickSettingStatus?.exitImageView?.setOnClickListener {
            addAlertDialog()
        }
        binding?.brightness?.brightnessSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                Log.d("TAG", " seek Bar value: $progress")
                Settings.System.putInt(contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        binding?.brightness?.brightnessAuto?.setOnCheckedChangeListener { buttonView, isChecked ->
            when(isChecked) {
                true -> {
                    Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)
                }
                false -> {
                    Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
                }
            }
        }

        updateDateTime()

        binding!!.topCoordinator.setOnClickListener {
            topSheetBehavior.state = TopSheetBehavior.STATE_COLLAPSED
        }

        topSheetBehavior.state = TopSheetBehavior.STATE_COLLAPSED

        observeBrightness()
        setBrightnessValue()

    }

    private fun observeBrightness() {
        val contentResolver = contentResolver
        val setting: Uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE)

        observer = object : ContentObserver(Handler()) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                setBrightnessValue()

            }

            override fun deliverSelfNotifications(): Boolean {
                return true
            }
        }

        contentResolver.registerContentObserver(setting, false, observer)
    }

    private fun setBrightnessValue() {
        val progress = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        Log.d("Tiles", " new brightness value $progress")
        binding?.brightness?.brightnessSeekBar?.progress = progress
    }

    fun updateDateTime() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            disposable.add(Observable.interval(1, TimeUnit.MINUTES)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe {
                        setDateTime()
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        setDateTime()
                    })

        } else {
            TODO("VERSION.SDK_INT < O")
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDateTime() {
        val date = LocalDateTime.now()
        binding?.quickSettingStatus?.dateText?.text = "${date.dayOfMonth} ${date.month} ${date.year}"
        binding?.quickSettingStatus?.timeText?.text = "${date.hour}:${date.minute}"
    }

    private fun normalize(x: Float, inMin: Float, inMax: Float, outMin: Float, outMax: Float): Int {
        val outRange = outMax - outMin
        val inRange = inMax - inMin
        return ((x - inMin) * outRange / inRange + outMin).roundToInt()
    }

    fun  addAlertDialog() {

        val localLayoutParams = WindowManager.LayoutParams()

        localLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        localLayoutParams.gravity = Gravity.CENTER
        localLayoutParams.flags = FLAG_NOT_TOUCH_MODAL  // this is to enable the notification to receive touch events


        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        localLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        localLayoutParams.format = PixelFormat.TRANSPARENT

        windowManager.addView(alertbinding!!.root, localLayoutParams)

        alertbinding.btnOk.setOnClickListener {
            val text = alertbinding!!.editTextTextPassword.text.toString()
            if(text == "ais1997") {
                windowManager.removeView(alertbinding!!.root)
                closeQuickSettingMenu()
            }
        }

        alertbinding.btnCancel.setOnClickListener {
            windowManager.removeView(alertbinding!!.root)
        }

        alertbinding.parentDialogLayout.setOnClickListener {
            windowManager.removeView(alertbinding!!.root)
        }
    }


    fun closeQuickSettingMenu() {
        windowManager.removeView(binding!!.root)
        stopForeground(true)
        enableSystemUi()


        try {
            unregisterReceiver(exitReceiver)
        } catch (e: Exception) {}
    }

    private fun enableSystemUi() {
//        Runtime.getRuntime().exec("su")
        Log.d("Amit", " Enabling system ui")
//        Runtime.getRuntime().exec("pm enable com.android.systemui")


        sharedpreferences.edit().apply {
            putBoolean("DISABLE_STATE", false)
        }.commit()

        Observable.timer(5 * 1000, TimeUnit.MILLISECONDS)
                .subscribe({
                    Log.d("Amit", " Enabling system ui")
                    val p = Runtime.getRuntime().exec("su")
                    val os = DataOutputStream(p.outputStream)
                    os.writeBytes("pm enable com.android.systemui" + "\n")
                    os.writeBytes("reboot" + "\n")
                    os.writeBytes("exit\n")
                    os.flush()

                }, {})
    }


    override fun onDestroy() {
        super.onDestroy()
        tilesAdapter.onCleared()
        contentResolver.unregisterContentObserver(observer)
        disposable.clear()
    }
}