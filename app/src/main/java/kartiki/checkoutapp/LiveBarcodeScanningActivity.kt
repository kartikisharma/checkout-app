/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kartiki.checkoutapp

import android.Manifest
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.internal.Objects
import com.google.android.material.chip.Chip
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kartiki.checkoutapp.barcodedetection.BarcodeProcessor
import kartiki.checkoutapp.camera.CameraSource
import kartiki.checkoutapp.camera.CameraSourcePreview
import kartiki.checkoutapp.camera.GraphicOverlay
import kartiki.checkoutapp.camera.WorkflowModel
import kartiki.checkoutapp.camera.WorkflowModel.WorkflowState
import kartiki.checkoutapp.network.HerokuService
import kartiki.checkoutapp.network.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import com.google.android.material.snackbar.Snackbar
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback


/** Demonstrates the barcode scanning workflow using camera preview.  */
class LiveBarcodeScanningActivity : AppCompatActivity(), OnClickListener {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://shielded-refuge-24263.herokuapp.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    private val service = retrofit.create(HerokuService::class.java)

    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    //    private var settingsButton: View? = null
//    private var flashButton: View? = null
    private var promptChip: Chip? = null
    private var promptChipAnimator: AnimatorSet? = null
    private var workflowModel: WorkflowModel? = null
    private var currentWorkflowState: WorkflowModel.WorkflowState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_live_barcode_kotlin)
        preview = findViewById(R.id.camera_preview)
        graphicOverlay = findViewById<GraphicOverlay>(R.id.camera_preview_graphic_overlay).apply {
            setOnClickListener(this@LiveBarcodeScanningActivity)
            cameraSource = CameraSource(this)
        }

        promptChip = findViewById(R.id.bottom_prompt_chip)
        promptChipAnimator =
            (AnimatorInflater.loadAnimator(this, R.animator.bottom_prompt_chip_enter) as AnimatorSet).apply {
                setTarget(promptChip)
            }

//        findViewById<View>(R.id.close_button).setOnClickListener(this)
//        flashButton = findViewById<View>(R.id.flash_button).apply {
//            setOnClickListener(this@LiveBarcodeScanningActivity)
//        }
//        settingsButton = findViewById<View>(R.id.settings_button).apply {
//            setOnClickListener(this@LiveBarcodeScanningActivity)
//        }

        setUpWorkflowModel()
    }

    override fun onResume() {
        super.onResume()

        workflowModel?.markCameraFrozen()
//        settingsButton?.isEnabled = true
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED
        cameraSource?.setFrameProcessor(BarcodeProcessor(graphicOverlay!!, workflowModel!!))
        workflowModel?.setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
    }

//    override fun onPostResume() {
//        super.onPostResume()
////        BarcodeResultFragment.dismiss(supportFragmentManager)
//    }

    override fun onPause() {
        super.onPause()
        currentWorkflowState = WorkflowState.NOT_STARTED
        stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
        cameraSource = null
    }

    override fun onClick(view: View) {
        when (view.id) {
//            R.id.close_button -> onBackPressed()
//            R.id.flash_button -> {
//                flashButton?.let {
//                    if (it.isSelected) {
//                        it.isSelected = false
//                        cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF)
//                    } else {
//                        it.isSelected = true
//                        cameraSource!!.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
//                    }
//                }
//            }
//            R.id.settings_button -> {
//                settingsButton?.isEnabled = false
//                startActivity(Intent(this, SettingsActivity::class.java))
//            }
        }
    }

    private fun startCameraPreview() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //ask for authorisation
            val permissions = arrayOf(Manifest.permission.CAMERA)
            ActivityCompat.requestPermissions(this, permissions, 50);
        } else {

        }
        val workflowModel = this.workflowModel ?: return
        val cameraSource = this.cameraSource ?: return
        if (!workflowModel.isCameraLive) {
            try {
                workflowModel.markCameraLive()
                preview?.start(cameraSource)
            } catch (e: IOException) {
                Log.e(TAG, "Failed to start camera preview!", e)
                cameraSource.release()
                this.cameraSource = null
            }
        }
    }

    private fun stopCameraPreview() {
        val workflowModel = this.workflowModel ?: return
        if (workflowModel.isCameraLive) {
            workflowModel.markCameraFrozen()
//            flashButton?.isSelected = false
            preview?.stop()
        }
    }

    private fun setUpWorkflowModel() {
        workflowModel = ViewModelProviders.of(this).get(WorkflowModel::class.java)

        // Observes the workflow state changes, if happens, update the overlay view indicators and
        // camera preview state.
        workflowModel!!.workflowState.observe(this, Observer { workflowState ->
            if (workflowState == null || Objects.equal(currentWorkflowState, workflowState)) {
                return@Observer
            }

            currentWorkflowState = workflowState
            Log.d(TAG, "Current workflow state: ${currentWorkflowState!!.name}")

            val wasPromptChipGone = promptChip?.visibility == View.GONE

            when (workflowState) {
                WorkflowState.DETECTING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_point_at_a_barcode)
                    startCameraPreview()
                }
                WorkflowState.CONFIRMING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_move_camera_closer)
                    startCameraPreview()
                }
                WorkflowState.SEARCHING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_searching)
                    stopCameraPreview()
                }
                WorkflowState.DETECTED, WorkflowState.SEARCHED -> {
                    promptChip?.visibility = View.GONE
                    stopCameraPreview()
                }
                else ->
                    promptChip?.visibility = View.GONE
            }

            val shouldPlayPromptChipEnteringAnimation = wasPromptChipGone && promptChip?.visibility == View.VISIBLE
            promptChipAnimator?.let {
                if (shouldPlayPromptChipEnteringAnimation && !it.isRunning) it.start()
            }
        })

        workflowModel?.detectedBarcode?.observe(this, Observer { barcode ->
            if (barcode != null && !barcode.rawValue.isNullOrEmpty()) {
                //TODO lookup in the local database for whether it exists,

                GlobalScope.launch(Dispatchers.Main) {
                    val getItemRequest = service.getItemWithBarcode(barcode.rawValue!!)
                    try {
                        val response = getItemRequest.await()
                        val item = response.body()
                        onGetItemResponse(response, item)
                        startCameraPreview()
                    } catch (e: Exception) {
                        onFailure(e)
                    }
                }
            }
        })
    }

    private fun onFailure(e: Exception) {
        if (e is IOException) {
            Snackbar
                .make(findViewById(R.id.container), INTERNET_UNAVAILABLE, Snackbar.LENGTH_LONG)
                .show()
        } else {
            Snackbar
                .make(findViewById(R.id.container), CONVERSION_ISSUE, Snackbar.LENGTH_LONG)
                .show()
        }
    }

    private fun onGetItemResponse(
        response: Response<Item>,
        item: Item?
    ) {
        if (response.isSuccessful && item != null) {
            modifyItemAvailability(item)
        } else {
            // TODO if not -> redirect to add item to system with barcode filled in
            Snackbar
                .make(findViewById(R.id.container), BARCODE_MATCH_FAILURE, Snackbar.LENGTH_LONG)
                .show()
        }
    }

    private fun modifyItemAvailability(item: Item) {
        service.modifyItemsAvailability(!item.available, item.barcode).enqueue(
            object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    onFailure(IOException())
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (!response.isSuccessful) {
                        onFailure(IOException())
                    } else {
                        val status : String  = if (!item.available) "checked in" else "checked out"
                        val text = ITEM_STATUS.format(item.name, status)
                        Snackbar.make(findViewById(R.id.container), text, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        )
    }

    companion object {
        private const val TAG = "LiveBarcodeActivity"
        private const val INTERNET_UNAVAILABLE = "Internet unavailable"
        private const val CONVERSION_ISSUE = "conversion issue! big problems :("
        private const val BARCODE_MATCH_FAILURE = "Sorry, no item with this barcode was found!"
        private const val ITEM_STATUS = "Item, %s, was %s successfully"
    }
}
