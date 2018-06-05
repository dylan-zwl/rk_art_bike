package com.tapc.platform.workout;

public interface MessageType {
	// UI messages
	int MSG_UI_BASE = 0x2000;
	int MSG_UI_HOME = MSG_UI_BASE + 1;
	int MSG_UI_MAIN_PAGE_PRE = MSG_UI_BASE + 2;
	int MSG_UI_MAIN_PAGE_NEXT = MSG_UI_BASE + 3;
	int MSG_UI_MAIN_STOP = MSG_UI_BASE + 4;
	int MSG_UI_MAIN_START = MSG_UI_BASE + 5;
	int MSG_UI_MAIN_LANGUAGE = MSG_UI_BASE + 6;
	int MSG_UI_MAIN_SHOW_RF = MSG_UI_BASE + 7;
	int MSG_UI_SHOW_QR_DIALOG = MSG_UI_BASE + 8;
	int MSG_UI_SHOW_DEVICE_DIALOG = MSG_UI_BASE + 10;
}
