package com.youth4work.yassess_new.network;


import androidx.annotation.NonNull;

import com.youth4work.yassess_new.network.model.GetUserDetailsModel;
import com.youth4work.yassess_new.network.model.Question;
import com.youth4work.yassess_new.network.model.TestModel;
import com.youth4work.yassess_new.network.model.User;
import com.youth4work.yassess_new.network.model.request.CaptureImageRequest;
import com.youth4work.yassess_new.network.model.request.LoginRequest;
import com.youth4work.yassess_new.network.model.request.UserRegister;
import com.youth4work.yassess_new.network.model.response.CheckPasskeyStatusResp;
import com.youth4work.yassess_new.network.model.response.EditprofileResponse;
import com.youth4work.yassess_new.network.model.response.GetAllFDTestModel;
import com.youth4work.yassess_new.network.model.response.GetYouthDetailsModel;
import com.youth4work.yassess_new.network.model.response.InstructionInfoModel;
import com.youth4work.yassess_new.network.model.response.LoginResponse;
import com.youth4work.yassess_new.network.model.response.SampleQuestionModel;
import com.youth4work.yassess_new.network.model.response.TestDetailsModel;
import com.youth4work.yassess_new.network.model.response.YassesgetTestModel;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface yAssessServices {

   @NonNull
    @POST("authenticate")
    Call<LoginResponse> getAuth(@Body LoginRequest loginRequest);

 @NonNull
 @POST("doSignUp")
 Call<Long> doSignUp(@Body UserRegister userRegister);

 @NonNull
 @GET("doSignIn")
 Call<User> doSignIn(
         @Query("user") String username,
         @Query("pwd") String password);

 @NonNull
 @GET("isEmailIdExists/{email}")
 Call<ResponseBody> emailIDExists(
         @Path("email") String email);

 @NonNull
 @GET("isMobileExists/{mobileno}")
 Call<ResponseBody> mobileExists(
         @Path("mobileno") String mobileno);

 @NonNull
 @GET("isUserNameExists/{username}")
 Call<ResponseBody> usernameExists(
         @Path("username") String username);

 @NonNull
 @GET("CheckEmail")
 Call<ResponseBody> CheckEmail(
         @Query("emailid") String emailid);

 @NonNull
 @GET("VerifyForgetPwdDtl")
 Call<GetUserDetailsModel> VerifyForgetPwdDtl(
         @Query("searchText") String searchText);

 @NonNull
 @GET("verifynsendcodemobile")
 Call<Boolean> verifynsendcodemobile(
         @Query("mobile") String mobile);

 @NonNull
 @GET("verifynsendcodeemail")
 Call<ResponseBody> verifynsendcodeemail(
         @Query("email") String email);

 @NonNull
 @GET("checkverificationcode")
 Call<Boolean> checkverificationcode(
         @Query("otp") String otp,
         @Query("Verifytype") String Verifytype);

 @NonNull
 @GET("changenewpassword")
 Call<Boolean> changenewpassword(
         @Query("UserId") long UserId,
         @Query("currentpwd") String currentpwd,
         @Query("newpwd") String newpwd);

 @NonNull
 @GET("yAsessments")
 Call<TestModel> yAsessments(
         @Query("Userid") long UserId,
         @Query("EmailId") String EmailId,
         @Query("PageIndex") int PageIndex,
         @Query("PageSize") int PageSize);

 @NonNull
 @GET("GetyAsessQuestion")
 Call<Question> GetyAsessQuestion(
         @Query("Userid") long UserId,
         @Query("TestId") int TestId,
         @Query("count") int count,
         @Query("result") int result,
         @Query("rating") int rating);

 @NonNull
 @POST("SubmitTest")
 Call<Boolean> SubmitTest(
         @Query("Userid") long UserId,
         @Query("TestId") int TestId);

 @NonNull
 @GET("yadSubmit")
 Call<Boolean> yadSubmit(
         @Query("TestId") int TestId,
         @Query("Userid") long UserId);

 @NonNull
 @GET("yScore")
 Call<Integer> yScore(
         @Query("TestId") int TestId,
         @Query("Userid") long UserId);

 @NonNull
 @GET("IsCorect")
 Call<ResponseBody> IsCorect(
         @Query("Userid") long UserId,
         @Query("Correct") String CorrectId,
         @Query("AnswerID") String AnswerID,
         @Query("Timetaken") String Timetaken,
         @Query("guid") String guid,
         @Query("questionid") String questionid,
         @Query("time2solve") String time2solve);

 @NonNull
 @GET("CheckPasskeyStatus")
Call<CheckPasskeyStatusResp>CheckPasskeyStatus(
        @Query("txtpasskey") String txtpasskey);

@NonNull
 @POST("UpdateInvitation")
 Call<Boolean>UpdateInvitation(
         @Query("Passkey") String Passkey,
         @Query("UserId") Long UserId);

@NonNull
 @GET("Practice")
 Call<TestDetailsModel>Practice(
         @Query("testid") int testid);

@NonNull
 @GET("Instruction")
 Call<ResponseBody>Instruction(
        @Query("testid") int testid,
        @Query("UserId") Long UserId);

@NonNull
 @GET("InstructionInfo")
 Call<InstructionInfoModel>InstructionInfo(
        @Query("testid") int testid,
        @Query("EmailId") String emailId);

@NonNull
 @GET("SampleQuestion")
 Call<SampleQuestionModel>SampleQuestion(
        @Query("testid") int testid,
        @Query("UserId") Long UserId,
        @Query("No") int No);

@NonNull
 @GET("yassesgetTest")
 Call<List<YassesgetTestModel>>yassesgetTest(
        @Query("testid") int testid,
        @Query("UserId") Long UserId);

@NonNull
 @GET("GetYouthDetails")
 Call<GetYouthDetailsModel>GetYouthDetails(
        @Query("testid") int testid,
        @Query("UserId") Long UserId);

@NonNull
 @POST("CaptureImage")
 Call<Boolean>CaptureImage(@Body
 CaptureImageRequest captureImageRequest);

 @NonNull
 @GET("yassesFDTest")
 Call<ResponseBody>yassesFDTest(
         @Query("TestId") int testid,
         @Query("UserId") Long UserId);

 @NonNull
 @GET("GetAllFDTest")
 Call<GetAllFDTestModel>GetAllFDTest(
         @Query("TestId") int testid,
         @Query("UserId") Long UserId);

 @NonNull
 @GET("StartTestFd")
 Call<Integer> StartTestFd(
         @Query("TestId") int testid,
         @Query("UserId") Long userId);

 @NonNull
 @GET("PushAnswerFDTest")
 Call<ResponseBody> PushAnswerFDTest(@Query("Userid") long UserId,
                                        @Query("Correct") String CorrectId,
                                        @Query("AnswerID") String AnswerID,
                                        @Query("Timetaken") String Timetaken,
                                        @Query("questionid") String questionid,
                                        @Query("time2solve") String time2solve);

 @NonNull
 @GET("UpdateAnswerFDTest")
 Call<ResponseBody> UpdateAnswerFDTest(@Query("Userid") long UserId,
                                          @Query("Correct") String CorrectId,
                                          @Query("AnswerID") String AnswerID,
                                          @Query("Timetaken") String Timetaken,
                                          @Query("questionid") String questionid,
                                          @Query("time2solve") String time2solve);

@NonNull
 @GET("Editprofile")
 Call<EditprofileResponse> Editprofile(
         @Query("TestId") int TestId);

 @NonNull
 @GET("GetYouthProfile")
 Call<User> GetYouthProfile(@Query("Userid") long UserId);

 @NonNull
 @POST("SummitEditprofile")
 Call<Boolean>SummitEditprofile(@Query("UserId") long UserId,
                                     @Query("Sex") String Sex,
                                     @Query("AadharNo") String AadharNo,
                                     @Query("HeadLine") String HeadLine);
}
