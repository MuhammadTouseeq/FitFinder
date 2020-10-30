package com.highbryds.fitfinder.vm.UserChatting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.model.Chatting
import com.highbryds.fitfinder.model.UsersData
import com.highbryds.fitfinder.retrofit.ApiInterface
import com.highbryds.fitfinder.room.Tables.UserChat
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserChattingViewModel @Inject constructor(private val provideApiInterface: ApiInterface,
private  val getDatabase: com.highbryds.fitfinder.room.Dao) : ViewModel() {

    lateinit var apiResponseCallBack: ApiResponseCallBack
    var userMsgs: LiveData<List<UserChat>> = MutableLiveData()


    fun sendChat(chatting: Chatting){
        viewModelScope.launch {
            sendChatCall(chatting)
        }
    }

    fun getMSG(){
        viewModelScope.launch {
            getChatItems().let {
            //   userMsgs as MutableLiveData
                userMsgs = it
            }
        }
    }


    private suspend fun sendChatCall(chatting: Chatting){
        try {
            val response = provideApiInterface.carpoolSendMessage(chatting)
            if (response.isSuccessful){
                apiResponseCallBack.getSuccess("Message Send")
            }else{
                apiResponseCallBack.getError("Message Error")
            }
        }catch (e: Exception){
            apiResponseCallBack.getError(e.toString())
        }
    }

    private suspend fun getChatItems() : LiveData<List<UserChat>>{
        return getDatabase.getallChat("", "")!!
    }
}