package makeFileTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * まだディレクトリも存在しないところにたくさんファイルを作る時には，
 * まず，書き込み先のファイル名をとってきて，getParent()でそれまでのディレクトリをもってきて，
 * dirParent.mkdirs()でそれまでのディレクトリ構造を作って，そっからファイルに書き込み．
 * ファイル自体は存在しなくても怒られない．
 * @author kajiwarakengo
 *
 */
public class MakeFile {

	public MakeFile() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public static void main(String[] args) throws IOException {
		String filePath = "test/innner/test.txt";
		File file = new File(filePath);
		
		String dirParent = file.getParent();
		new File(dirParent).mkdirs();
		
		FileWriter fw = new FileWriter(file);
		fw.write("hello!");
		fw.close();
		
	}

}
