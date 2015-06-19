package com.castlefrog.games.asg

import android.app.Activity
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.caverock.androidsvg.SVG

public class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val add = findViewById(R.id.add) as FloatingActionButton;
        add.setImageSvg(R.raw.ic_add)
        add.setOnClickListener {
            //val fragmentTransaction = getFragmentManager().beginTransaction()
            //fragmentTransaction.replace(R.id.container, SelectGameTypeFragment())
            //fragmentTransaction.commit()
            SelectGameTypeFragment().show(getFragmentManager(), null)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun ImageView.setImageSvg(resId: Int) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        val svg = SVG.getFromResource(getResources(), resId);
        setImageDrawable(PictureDrawable(svg.renderToPicture()));
    }
}