package com.anwesh.uiprojects.linkedjumpconsecutiveboxview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.jumpconsecutiveboxview.JumpConsecutiveBoxView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        JumpConsecutiveBoxView.create(this)
    }
}
