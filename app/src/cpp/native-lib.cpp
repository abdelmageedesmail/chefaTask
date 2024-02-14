#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_jobzella_chefatask_data_modules_repository_MarvelComicsRepositoryImpl_getPrivateKey(
        JNIEnv *env, jobject thiz) {

}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_abdelmageed_chefatask_data_modules_repository_MarvelComicsRepositoryImpl_getPublicKey(
        JNIEnv *env, jobject thiz) {
    std::string publicKey = "abdce80808b0d120a815134c5256c759";
    return env->NewStringUTF(publicKey.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_abdelmageed_chefatask_data_modules_repository_MarvelComicsRepositoryImpl_getPrivateKey(
        JNIEnv *env, jobject thiz) {
    std::string privateKey = "6bbea70c88145e66128f9eebdaf3391a43d18daa";
    return env->NewStringUTF(privateKey.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_abdelmageed_chefatask_data_common_RetrofitModule_getBaseUrl(JNIEnv *env, jobject thiz) {
    std::string baseURL = "https://gateway.marvel.com/v1/";
    return env->NewStringUTF(baseURL.c_str());
}