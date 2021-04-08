package com.highbryds.fitfinder.vm.UserChatting

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.model.Chatting
import com.highbryds.fitfinder.model.ChattingMsgTo
import com.highbryds.fitfinder.retrofit.ApiInterface
import com.highbryds.fitfinder.room.Tables.UserChat
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserChattingViewModel @ViewModelInject constructor(
    private val provideApiInterface: ApiInterface,
    private val getDatabase: com.highbryds.fitfinder.room.Dao
) : ViewModel() {

    lateinit var apiResponseCallBack: ApiResponseCallBack

    var msgTo: MutableLiveData<List<ChattingMsgTo>> = MutableLiveData()
    var userMsgs: LiveData<List<UserChat>> = MutableLiveData()
    var userGroupMsgs: LiveData<List<UserChat>> = MutableLiveData()

    init {
       // getDatabase.getmsgs()
        getGroupMsgs()
    }

    fun sendChat(chatting: Chatting) {
        viewModelScope.launch {
            sendChatCall(chatting)
        }
    }

    fun getMSG(recpID: String, sendID: String) {
        viewModelScope.launch {
            getChatItems(recpID, sendID).let {
                //   userMsgs as MutableLiveData
                userMsgs = it
            }
        }
    }

    fun getGroupMsgs() {
        viewModelScope.launch {
            getChatGroup().let {
                //   userMsgs as MutableLiveData
                userGroupMsgs = it
            }
        }
    }


    private suspend fun sendChatCall(chatting: Chatting) {
        try {
            val response = provideApiInterface.carpoolSendMessage(chatting)
            if (response.isSuccessful) {

                msgTo.value = response.body()?.chatting
                apiResponseCallBack.getSuccess("Message Sent")
            } else {
                apiResponseCallBack.getError("Error occured while sending msg.")
            }
        } catch (e: Exception) {
            apiResponseCallBack.getError(e.toString())
        }
    }

    private suspend fun getChatItems(recpID: String, sendID: String): LiveData<List<UserChat>> {
        return getDatabase.getallChat(recpID, sendID)!!
        // return getDatabase.getallChat()!!
    }

    private suspend fun getChatGroup(): LiveData<List<UserChat>> {
        return getDatabase.getmsgs()
        // return getDatabase.getallChat()!!
    }
}