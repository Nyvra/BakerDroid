package net.nyvra.bakerdroid;

public class BakerDroidConfigs {
	public enum StorageMode {STORAGE_ASSETS_FOLDER, STORAGE_EXTERNAL}
	
	private static StorageMode sStorageMode = StorageMode.STORAGE_ASSETS_FOLDER;
	private static boolean sOvewriteContentIfChanged = false;
	
	public static StorageMode getStorageMode() {
		return sStorageMode;
	}
	
	public static void setStorageMode(StorageMode sStorageMode) {
		BakerDroidConfigs.sStorageMode = sStorageMode;
	}
	
	public static boolean shouldOvewriteContentIfChanged() {
		return sOvewriteContentIfChanged;
	}
	
	public static void ovewriteContentIfChanged(boolean ovewriteContentIfChanged) {
		BakerDroidConfigs.sOvewriteContentIfChanged = ovewriteContentIfChanged;
	}

}
