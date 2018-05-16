package com.smartdevicelink.api;

import android.content.Context;

import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.protocol.enums.SessionType;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.SdlProxyBase;
import com.smartdevicelink.proxy.callbacks.OnServiceEnded;
import com.smartdevicelink.proxy.callbacks.OnServiceNACKed;
import com.smartdevicelink.proxy.interfaces.IAudioStreamListener;
import com.smartdevicelink.proxy.interfaces.ISdl;
import com.smartdevicelink.proxy.interfaces.ISdlServiceListener;
import com.smartdevicelink.proxy.interfaces.IVideoStreamListener;
import com.smartdevicelink.proxy.interfaces.OnSystemCapabilityListener;
import com.smartdevicelink.proxy.rpc.SdlMsgVersion;
import com.smartdevicelink.proxy.rpc.enums.AppHMIType;
import com.smartdevicelink.proxy.rpc.enums.Language;
import com.smartdevicelink.proxy.rpc.enums.SdlDisconnectedReason;
import com.smartdevicelink.proxy.rpc.enums.SystemCapabilityType;
import com.smartdevicelink.proxy.rpc.listeners.OnMultipleRequestListener;
import com.smartdevicelink.proxy.rpc.listeners.OnRPCNotificationListener;
import com.smartdevicelink.streaming.audio.AudioStreamingCodec;
import com.smartdevicelink.streaming.audio.AudioStreamingParams;
import com.smartdevicelink.streaming.video.VideoStreamingParameters;
import com.smartdevicelink.transport.BaseTransportConfig;

import java.util.List;
import java.util.Vector;

/**
 * <strong>SDLManager</strong> <br>
 *
 * This is the main point of contact between an application and SDL <br>
 *
 * It is broken down to these areas: <br>
 *
 * 1. SDLManagerBuilder <br>
 * 2. ISdl Interface along with its overridden methods - This can be passed into attached managers <br>
 * 3. Sending Requests <br>
 * 4. Helper methods
 */
public class SdlManager implements ProxyBridge.LifecycleListener {

	private static String TAG = "Sdl Manager";
	private SdlProxyBase proxy;

	// Required parameters for builder
	private String appId, appName;
	private boolean isMediaApp;
	private Language hmiLanguage;
	private Vector<AppHMIType> hmiTypes;
	private BaseTransportConfig transport;
	private Context context;

	private ProxyBridge proxyBridge;
	//public LockScreenConfig lockScreenConfig;

	// Managers
	/*
	private FileManager fileManager;
	private VideoStreamingManager videoStreamingManager;
	private AudioStreamManager audioStreamManager;
	private LockscreenManager lockscreenManager;
	private ScreenManager screenManager;
	private PermissionManager permissionManager;
	*/

	private SdlManager() {}

	private void initialize(){
		// proxy bridge
		this.proxyBridge = new ProxyBridge(this);
		// instantiate managers
		/*
		this.fileManager = new FileManager(_internalInterface, context);
		this.lockscreenManager = new LockscreenManager(lockScreenConfig, context, _internalInterface);
		this.screenManager = new ScreenManager(_internalInterface, this.fileManager);
		this.permissionManager = new PermissionManager(_internalInterface);
		this.videoStreamingManager = new VideoStreamingManager(context, _internalInterface);
		this.audioStreamManager = new AudioStreamManager(_internalInterface);
		*/
	}

	private void dispose() {
		/*
		this.fileManager.dispose();
		this.lockscreenManager.dispose();
		this.audioStreamManager.dispose();
		this.screenManager.dispose();
		this.permissionManager.dispose();
		this.videoStreamingManager.dispose();
		this.audioStreamManager.dispose();
				 */
	}

	// BUILDER

	public static class Builder {
		SdlManager sdlManager;

		public Builder(){
			sdlManager = new SdlManager();
		}

		public Builder setAppId(final String appId){
			sdlManager.appId = appId;
			return this;
		}

		public Builder setAppName(final String appName){
			sdlManager.appName = appName;
			return this;
		}

		public Builder setIsMediaApp(final Boolean isMediaApp){
			sdlManager.isMediaApp = isMediaApp;
			return this;
		}

		public Builder setLanguage(final Language hmiLanguage){
			sdlManager.hmiLanguage = hmiLanguage;
			return this;
		}

		/*public Builder setLockScreenConfig (final LockScreenConfig lockScreenConfig){
			sdlManager.lockScreenConfig = lockScreenConfig;
			return this;
		}*/

		public Builder setHMITypes(final Vector<AppHMIType> hmiTypes){
			sdlManager.hmiTypes = hmiTypes;
			return this;
		}

		/**
		 * This Object type may change with the transport refactor
		 */
		public Builder setTransportType(BaseTransportConfig transport){
			sdlManager.transport = transport;
			return this;
		}

		public Builder setContext(Context context){
			sdlManager.context = context;
			return this;
		}

		@SuppressWarnings("unchecked")
		public SdlManager build() {
			try {
				sdlManager.initialize();
				sdlManager.proxy = new SdlProxyBase(sdlManager.proxyBridge, sdlManager.appName, sdlManager.isMediaApp, sdlManager.hmiLanguage, sdlManager.hmiLanguage, sdlManager.hmiTypes, sdlManager.appId, sdlManager.transport ) {};
			} catch (SdlException e) {
				e.printStackTrace();
			}
			return sdlManager;
		}
	}

	// MANAGER GETTERS

	/*public FileManager getFileManager() {
		return fileManager;
	}

	public VideoStreamingManager getVideoStreamingManager() {
		return videoStreamingManager;
	}

	public AudioStreamManager getAudioStreamManager() {
		return audioStreamManager;
	}

	public ScreenManager getScreenManager() {
		return screenManager;
	}

	public LockscreenManager getLockscreenManager() {
		return lockscreenManager;
	}

	public PermissionManager getPermissionManager() {
		return permissionManager;
	}*/

	// SENDING REQUESTS

	public void sendRPCRequest(RPCRequest request) throws SdlException {
		proxy.sendRPCRequest(request);
	}

	public void sendSequentialRequests(final List<? extends RPCRequest> rpcs, final OnMultipleRequestListener listener) throws SdlException {
		proxy.sendSequentialRequests(rpcs, listener);
	}

	public void sendRequests(List<? extends RPCRequest> rpcs, final OnMultipleRequestListener listener) throws SdlException {
		proxy.sendRequests(rpcs, listener);
	}

	// LIFECYCLE / OTHER

	@Override
	public void onProxyClosed(String info, Exception e, SdlDisconnectedReason reason){
		this.dispose();
	}

	@Override
	public void onServiceEnded(OnServiceEnded serviceEnded){

	}

	@Override
	public void onServiceNACKed(OnServiceNACKed serviceNACKed){

	}

	@Override
	public void onError(String info, Exception e){

	}

	// INTERNAL INTERFACE

	private ISdl _internalInterface = new ISdl() {
		@Override
		public void start() {
			try{
				proxy.initializeProxy();
			}catch (SdlException e){
				e.printStackTrace();
			}
		}

		@Override
		public void stop() {
			try{
				proxy.dispose();
			}catch (SdlException e){
				e.printStackTrace();
			}
		}

		@Override
		public boolean isConnected() {
			return proxy.getIsConnected();
		}

		@Override
		public void addServiceListener(SessionType serviceType, ISdlServiceListener sdlServiceListener) {
			proxy.addServiceListener(serviceType,sdlServiceListener);
		}

		@Override
		public void removeServiceListener(SessionType serviceType, ISdlServiceListener sdlServiceListener) {
			proxy.removeServiceListener(serviceType,sdlServiceListener);
		}

		@Override
		public void startVideoService(VideoStreamingParameters parameters, boolean encrypted) {
			if(proxy.getIsConnected()){
				proxy.startVideoStream(encrypted,parameters);
			}
		}

		@Override
		public IVideoStreamListener startVideoStream(boolean isEncrypted, VideoStreamingParameters parameters){
			return proxy.startVideoStream(isEncrypted, parameters);
		}

		@Override
		public void stopVideoService() {
			if(proxy.getIsConnected()){
				proxy.endVideoStream();
			}
		}

		@Override
		public void startAudioService(boolean isEncrypted, AudioStreamingCodec codec,
									  AudioStreamingParams params) {
			if(proxy.getIsConnected()){
				proxy.startAudioStream(isEncrypted, codec, params);
			}
		}

		@Override
		public IAudioStreamListener startAudioStream(boolean isEncrypted, AudioStreamingCodec codec,
													 AudioStreamingParams params) {
			return proxy.startAudioStream(isEncrypted, codec, params);
		}

		@Override
		public void stopAudioService() {
			if(proxy.getIsConnected()){
				proxy.endAudioStream();
			}
		}

		@Override
		public void sendRPCRequest(RPCRequest message){
			try {
				proxy.sendRPCRequest(message);
			} catch (SdlException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void addOnRPCNotificationListener(FunctionID notificationId, OnRPCNotificationListener listener) {
			proxy.addOnRPCNotificationListener(notificationId,listener);
		}

		@Override
		public boolean removeOnRPCNotificationListener(FunctionID notificationId, OnRPCNotificationListener listener) {
			return proxy.removeOnRPCNotificationListener(notificationId,listener);
		}

		@Override
		public Object getCapability(SystemCapabilityType systemCapabilityType){
			return proxy.getCapability(systemCapabilityType);
		}

		@Override
		public void getCapability(SystemCapabilityType systemCapabilityType, OnSystemCapabilityListener scListener) {
			proxy.getCapability(systemCapabilityType, scListener);
		}

		@Override
		public boolean isCapabilitySupported(SystemCapabilityType systemCapabilityType){
			return proxy.isCapabilitySupported(systemCapabilityType);
		}

		@Override
		public SdlMsgVersion getSdlMsgVersion(){
			return proxy.getSdlMsgVersion();
		}
	};

}