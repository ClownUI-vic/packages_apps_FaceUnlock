package co.aospa.sense.camera

import android.content.Context
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.SystemProperties
import java.util.ArrayList
import java.util.Comparator
import kotlin.math.abs

object CameraUtil {

    fun getBestPreviewSize(parameters: Camera.Parameters?, width: Int, height: Int): Camera.Size {
        val supportedPreviewSizes = parameters!!.supportedPreviewSizes
        val previewSizes = ArrayList<Camera.Size>()
        for (size in supportedPreviewSizes) {
            if (size.width > size.height) {
                previewSizes.add(size)
            }
        }
        previewSizes.sortWith(Comparator.comparingInt { size: Camera.Size ->
            abs(
                size.width * size.height - width * height
            )
        })
        return previewSizes[0]
    }

    fun getCameraId(context: Context?): Int {
        val cameraIdProp = SystemProperties.get("ro.face.sense_service.camera_id")
        if (!cameraIdProp.isNullOrEmpty()) {
            return cameraIdProp.toInt()
        }
        try {
            val cameraManager = context?.getSystemService(CameraManager::class.java)
            if (cameraManager == null) {
                return -1
            }
            val cameraIdList = cameraManager.cameraIdList
            if (cameraIdList.isNullOrEmpty()) {
                return -1
            }
            for (cameraId in cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val orientation = characteristics.get(CameraCharacteristics.LENS_FACING) ?: continue
                if (orientation == CameraCharacteristics.LENS_FACING_FRONT) {
                    return cameraId.toInt()
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return -1
    }
}
