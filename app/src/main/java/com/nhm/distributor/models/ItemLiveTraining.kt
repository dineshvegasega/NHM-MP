package com.nhm.distributor.models

data class ItemLiveTraining(
    val cover_image: CoverImage,
    var description: String,
    val district_id: Any,
    val educational_qualification: Any,
    val gender: Any,
    val live_link: String,
    val municipality_id: Any,
    var name: String,
    val select_demography: Boolean,
    val social_category: Any,
    val state_id: Any,
    val status: String,
    val training_end_at: String,
    val training_id: Int,
    val training_start_at: Any,
    val training_type: String,
    val training_video: TrainingVideo,
    val type_of_marketplace: Any,
    val type_of_vending: Any,
    val user_id: Any,
    val video_link: Any,
    val video_type: Any
)

data class CoverImage(
    val name: String,
    val url: String
)

data class TrainingVideo(
    val name: String,
    val url: String
)