package com.mobdeve.s18.mco.group9.studyshare

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s18.mco.group9.studyshare.databinding.MaterialDetailsBinding

class MaterialDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val viewBinding: MaterialDetailsBinding = MaterialDetailsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val materialName = intent.getStringExtra(IntentKeys.MATERIAL_NAME.name)
        val materialType = intent.getStringExtra(IntentKeys.MATERIAL_TYPE.name)
        val materialAuthor = intent.getStringExtra(IntentKeys.MATERIAL_AUTHOR.name)
        val materialDate = intent.getStringExtra(IntentKeys.MATERIAL_DATE.name)
        val colorIcon = intent.getStringExtra(IntentKeys.COLOR_ICON.name)

        viewBinding.materialNameTv.text = materialName
        viewBinding.materialDetailsTypeTv.text = materialType
        viewBinding.materialDetailsAuthorTv.text = materialAuthor
        viewBinding.materialDetailsDateTv.text = materialDate
        viewBinding.colormMaterialDetailsFrame.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorIcon))

    }
}

