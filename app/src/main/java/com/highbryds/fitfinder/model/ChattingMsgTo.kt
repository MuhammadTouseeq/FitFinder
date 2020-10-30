package com.highbryds.fitfinder.model


class ChattingMsgTo {
    var gender: String? = null
    var city: String? = null
    var isActive: String? = null
    var cellNumber: String? = null
    var about: String? = null
    var deviceToken: String? = null
    var createdAt: String? = null
    var socialId: String? = null
    var headline: String? = null
    var imageUrl: String? = null
    private var __v: String? = null
    var name: String? = null
    var emailAdd: String? = null
    var country: String? = null
    var socialType: String? = null
    private var _id: String? = null
    var age: String? = null

    fun get__v(): String? {
        return __v
    }

    fun set__v(__v: String?) {
        this.__v = __v
    }

    fun get_id(): String? {
        return _id
    }

    fun set_id(_id: String?) {
        this._id = _id
    }

    override fun toString(): String {
        return "ClassPojo [Gender = " + gender + ", City = " + city + ", isActive = " + isActive + ", cellNumber = " + cellNumber + ", About = " + about + ", deviceToken = " + deviceToken + ", createdAt = " + createdAt + ", SocialId = " + socialId + ", Headline = " + headline + ", imageUrl = " + imageUrl + ", __v = " + __v + ", name = " + name + ", emailAdd = " + emailAdd + ", Country = " + country + ", SocialType = " + socialType + ", _id = " + _id + ", age = " + age + "]"
    }
}
