package com.example.background.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R
import java.lang.Exception

class BlurWorker(private val context: Context, private val params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        //get the input passed input the work request
        val resourceUri = inputData.getString(KEY_IMAGE_URI)
        makeStatusNotification("Bluring Image", context)

        return try {
            if(TextUtils.isEmpty(resourceUri)){
                Log.e("EMPTY", "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }
            val picture = BitmapFactory.decodeStream(context.contentResolver.openInputStream(Uri.parse(resourceUri)))
            val output = blurBitmap(picture, context)
            //write the output to a temp file
            val outputUri = writeBitmapToFile(context, output)
            // You're now done with this Worker and can return the output
            // URI in Result.success(). Provide the Output URI as an output
            // Data to make this temporary image easily accessible to other
            // workers for further operations. This will be useful
            // in the next chapter when you create a Chain of workers.
            makeStatusNotification("Output is $outputUri", context)
            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())
            Result.success(outputData)
        } catch (e: Exception){
            Log.e("ERROR", "${e.message}")
            Result.failure()
        }
    }
}

/*
* WorkManager Basics
There are a few WorkManager classes you need to know about:

Worker: This is where you put the code for the actual work you want
* to perform in the background. You'll extend this class and override the doWork() method.
WorkRequest: This represents a request to do some work.
* You'll pass in your Worker as part of creating your WorkRequest.
* When making the WorkRequest you can also specify things like Constraints
* on when the Worker should run.
WorkManager: This class actually schedules your WorkRequest and
* makes it run. It schedules WorkRequests in a way that spreads out the
* load on system resources, while honoring the constraints you specify.
 */