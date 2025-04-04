package obs1d1anc1ph3r.reverseshell.server.plugins.reactive.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSaver {

	public void saveFile(byte[] fileBytes, Socket clientSocket, String fileName) {
		try {
			//Save based on ip
			String clientIP = clientSocket.getInetAddress().toString().replace("/", "");
			Path saveDir = Paths.get(clientIP); //The directory is the ip of the client
			if (Files.notExists(saveDir)) { //If it doesn't exist it makes it
				Files.createDirectories(saveDir);
				System.out.println("[INFO] Created directory for client: " + saveDir.toAbsolutePath());
			}

			File downloadedFile = new File(saveDir.toFile(), fileName); //Save it
			try (FileOutputStream fos = new FileOutputStream(downloadedFile)) {
				fos.write(fileBytes); //Write the bytes
			}
			System.out.println("[INFO] File saved at: " + downloadedFile.getAbsolutePath());
			System.out.print("[-] Command> ");
		} catch (IOException ex) {
			System.err.println("[ERROR] Error saving file: " + ex.getMessage());
		}
	}
}

/*


¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨§§§¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨Æ§¨¨¨¨¨¨¨¨¨¨¨Æzzzzzzzzzzzzzzzzzzzz½ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨§Æ¨¨¨¨Æ¨¨¨¨·¨¨¨¨¨¨Æ¨¨¨¨¨¨Æ¨¨¨ÆzzzzzzzzzzzzzzzzzzzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆ¨¨·¨¨Æ¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨Æzz‡zzzzzzzzzzzzzzzzzzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨Æ¨¨¨¨¨û¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆzzzzzzz‡ÆÆzzzzzzzzzzzzz§ÆzÆLÆ’ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨Œ¨¨¨¨ÆÆ¨¨¨¨¨Æ¨¨¨¨¨¨¨¨¨·¨¨¨¨¨¨¨¨zzzzz‡Æzzzzzzzzzzzzzzzzzzzz‡·’’ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨Æ¨¨¨§Æ¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨·¨’¨¨¨¨‡zzÆzzzzzzzzzzzzzzzzzzzzzzzÆ···ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆ¨¨¨¨Æ¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨‡Æzzzzzzz‡zzzzzzzzzzzzzzzzÆ·ÆzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨N¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨Æzzzzzzzzzzzzzzzzzzzzzzzz··zzzzzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨“¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆzzzzJzzzzzzzzzzzzzzzzzzz··Jzzzzzzzzzz¤ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨Æ¨¨¨¨Æ¨¨¨¨¨·¨¨¨¨¨¨¨¨Izz‡zzzzzzz‡zzzzzzzzzzz‡··zzzzzzzzzzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆ¨¨¨¨¨Æ¨¨¨¨¨¨¨¨¨¨¨¨ÆzzzÆzzzzzzÆž·âzzzzzzzz5··IzzzzzzzzzzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨·¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆ¨¨¨¨¨¨‡¨¨¨¨¨¨¨¨¨¨Æzzzzzzzzzz…Æzzƒ··ÆÆz‡ð···ezzzzzzzzzzzzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÝ¨¨¨¨¨¨¨¨¨Æ¨¨¨¨ÆÆÆÆÆzzzzzzzz……Æzzz‡zÆ········NzzzzzzzzzzzzzzzJÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆ¨¨¨¨¸ÆÆÆÆÆÆ¨ÆÆÆÆÆÆÆÆÆz‡zzzz…………zzzzzzz‰Æ········zzzzzzzzzzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆ¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆzÆCzzÆ…………Æzzzzzzz‡zzzzÁÆi··ÆzzzzzzzzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆ¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆz……zzÆ……˜H…zzzzzzzzzzzzzzzzzzîzzzzzzzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆ¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨Æ¨ÆÆÆÆÆÆÆÆ………z………………&zzzzzzzÆzzzzzzzzzzzzzzzzzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÞÆÆÆÆÆÆÆÆÆ¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨Æ¨¨Æ¨ÆÆÆ…………………………………‡‡zzzz‡‡zzzzzzzzzzÆzzzzzzzzzÂÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆ¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆ­Æ…………………………………ÆzzzzzÆÆzzzzzzzzzzzzzzzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆ¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨M¨¨¨¨Æ………………Ä…ÁÄ:…………zzzzÈ…$‡zzzzz‡zzzzzzzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆ¨¨¨¨¨¨ÆÆ¨ÆÆ¨¨¨¨¨¨¨¨¨¨¨¨¨¨……………………………………………ÆÆÆ………Æzzzzzz½‡zzzzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆ¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨Æ……………………ˆ……¸………………………………ÆzzzzzIzzzzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆ¨¨¨ÆÆÆÆÆÆ¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨Æ¨¨¨¨……………………………………………………þÄÄ………Æzzzzzzzzzzz¼ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆ¨¨¨¨¨ÆÆÆÆÆÆ¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆ¨¨Æ…………………………………………………¸……………Æ¼zzz8zzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨·¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨Æ¨¨¨¨¨¨fÆÆÆÆÆÆÆÆ¨¨¨¨ÆÆ·¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨2Æ……………………………………………………………Æzz‡zzz‡zzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨’¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆ¨Æ¨ÆÆ…¨Æ¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨Æ––Æ……………………………………………………zzzzzzÆòzzzzzIÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆ¼ÆÆÆÆÆÆÆ¨Æ¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨Æ¨¨¨¨¨z–Æ²…Æ……………………………zzÆ…©zzzzzzzzzzzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ¨Æ¨¨¨·Æ¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆ––²……………ÆÆ……¶ÆÆÆîzzzzzzzƒzzzzzz‡zzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ¨ÆÆÆÆ¨¨¨¨¨¨¨¨ÆÆ¨¨¨¨ÆÆÆÆÆÆ––~…………………d––ÆÆÆÆÆ‡¼ÆÆ¼zzzzz‰zzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ––°………………–––ÆÆÆÆÆÆÆÆÆCzzzzzÆzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ¨Ñ¨¨Æ¨ÆÆÆÆÆÆÆ^–––––ÆÆÆÆÆ––Æ……………²––ÆÜ–²~ªÆÆÆÆzzzzzÆzzzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨·¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ–²––––––²ÆÆÆ–Ê–Æ……………Æ––––––––––ÆÆzzzz6zzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨·¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ–––––––Ñ––ÆÆÆ–––Æ………………Æ––Æ––––––²ÆÆ‡z%zzz‡ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ––––––Q–––ÆÆ²Æ–––Æ…………………V–º–––––²°ÆÆÆ£zzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ–––––––––ÆÆÆº–Æ–––ÆÆX……Æ‡zÆ–––––²––ÆÆÆÆzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨·¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆz–––––––²––Æ²––~–––Æzzzzzzz½–––––Æ–°ÆÆÆzzzÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨·¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ––––––²~–²Æ–––²––––Æzzzzzzzz––––²Æ–ÆÆÆÆ‡zÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨·¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ–ÆºÆÆ–––ºÆ––––Æ––––zzzzzzzzzº––––ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ–––––––ÆÆ––––²Æ–––Hzzzzzzzzz²––––ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨·¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ––––––Æ–––––––È––Æzzzzzzzzzz²––––ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ––––Æ¡––––––––E–ÆzzzzzzzzzzÆ²––––ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ––Æ†––––––––––ÆÄzzzzzzzzzzzŒ––––ºÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ––²––––––––––Æzzzzzzzzzzz‡–––––ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ––––––––––––––ÆzzzzzzzzzzzÆ–––––ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ~–²–––––––––––Æzzzzzzzzzz‡–––––ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ
¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ–––––––––––––Æzzzzzzzzzz~–––––ÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆÆ

(I'd be the one on the left)
 */
