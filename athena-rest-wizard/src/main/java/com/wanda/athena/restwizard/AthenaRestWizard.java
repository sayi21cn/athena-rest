package com.wanda.athena.restwizard;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.JOptionPane;

import com.wanda.athena.rest.util.FileUtils;
import com.wanda.athena.rest.util.StringUtils;

public class AthenaRestWizard extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Athena Rest Wizard");

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Scene scene = new Scene(grid, 500, 200);
		primaryStage.setScene(scene);

		Text scenetitle = new Text("Welcome, Wandaer!");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, 0, 2, 1);

		Label projectNameLabel = new Label("Project Name:");
		grid.add(projectNameLabel, 0, 1);

		final TextField projectName = new TextField();
		grid.add(projectName, 1, 1);

		Label projectDescLabel = new Label("Project Description:");
		grid.add(projectDescLabel, 0, 2);

		final TextField projectDesc = new TextField();
		grid.add(projectDesc, 1, 2);

		Button btn = new Button("Generate Service Project Now");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 4);

		final Text actiontarget = new Text();
		grid.add(actiontarget, 1, 6);

		btn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				try {
					doWizard(projectName.getText(), projectDesc.getText(),
							actiontarget);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});

		primaryStage.show();
	}

	private void doWizard(String name, String desc, Text actiontarget)
			throws IOException {
		// validate
		if (StringUtils.isBlank(name) || StringUtils.isBlank(desc)) {
			JOptionPane.showMessageDialog(null,
					"The name or description is empty. Please check",
					"Warning...", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// unzip file
		File root = new File("tmp");
		deleteDir(root);

		File fileZip = new File(root, "athena-example.zip");

		extractClasspathResource("/athena-example.zip",
				fileZip.getAbsolutePath());

		unZipIt(fileZip.getAbsolutePath(), root.getAbsolutePath());

		// replace names in poms
		File rootPro = new File(root, "athena-example");

		replace(new File(rootPro, "pom.xml"), "athena-example", name);
		replace(new File(rootPro, "pom.xml"), "Athena Example", desc);

		replace(new File(rootPro, "athena-example-data/pom.xml"),
				"athena-example", name);
		replace(new File(rootPro, "athena-example-data/pom.xml"),
				"Athena Example", desc);

		replace(new File(rootPro, "athena-example-rest/pom.xml"),
				"athena-example", name);
		replace(new File(rootPro, "athena-example-rest/pom.xml"),
				"Athena Example", desc);

		replace(new File(rootPro, "athena-example-service/pom.xml"),
				"athena-example", name);
		replace(new File(rootPro, "athena-example-service/pom.xml"),
				"Athena Example", desc);

		// rename folders
		new File(rootPro, "athena-example-data").renameTo(new File(rootPro,
				name + "-data"));
		new File(rootPro, "athena-example-rest").renameTo(new File(rootPro,
				name + "-rest"));
		new File(rootPro, "athena-example-service").renameTo(new File(rootPro,
				name + "-service"));

		// copy to desktop
		javax.swing.filechooser.FileSystemView fsv = javax.swing.filechooser.FileSystemView
				.getFileSystemView();
		copyDirectiory(rootPro.getAbsolutePath(), new File(fsv
				.getHomeDirectory().getAbsoluteFile(), name).getAbsolutePath());

		// rename project root folder
		rootPro.renameTo(new File(rootPro.getParentFile(), name));

		// show result
		actiontarget.setFill(Color.FIREBRICK);
		actiontarget.setText("Please find your project {" + name
				+ "} on your desktop.");
	}

	public void replace(File file, String src, String dest) throws IOException {
		File bakFile = new File(file.getPath() + ".bak");
		file.renameTo(bakFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(bakFile)));

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file)));
		try {
			String sLine = null;
			while ((sLine = br.readLine()) != null) {
				sLine = sLine.replaceAll(src, dest);

				bw.write(sLine);
				bw.newLine();
			}
		} finally {
			br.close();
			bw.close();
		}

		bakFile.delete();
	}

	public void unZipIt(String zipFile, String outputFolder) {

		byte[] buffer = new byte[1024];

		try {

			// create output directory is not exists
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
			}

			// get the zip file content
			ZipInputStream zis = new ZipInputStream(
					new FileInputStream(zipFile));
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {

				String fileName = ze.getName();
				if (!ze.isDirectory()) {

					File newFile = new File(outputFolder + File.separator
							+ fileName);

					System.out.println("file unzip : "
							+ newFile.getAbsoluteFile());

					// create all non exists folders
					// else you will hit FileNotFoundException for compressed
					// folder
					new File(newFile.getParent()).mkdirs();

					FileOutputStream fos = new FileOutputStream(newFile);

					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}

					fos.close();
				}
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			System.out.println("Done");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void extractClasspathResource(String classpathPath, String filePath)
			throws IOException {
		InputStream fileSteam = AthenaRestWizard.class
				.getResourceAsStream(classpathPath);

		FileUtils.esurePathExist(filePath);
		try {
			FileUtils.saveStream(fileSteam, new File(filePath));
		} finally {
			fileSteam.close();
		}
	}

	private boolean isClasspathResource(String classpathPath)
			throws IOException {
		InputStream fileStream = null;
		try {
			fileStream = AthenaRestWizard.class
					.getResourceAsStream(classpathPath);

			return fileStream != null;
		} finally {
			if (fileStream != null)
				fileStream.close();
		}
	}

	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();

			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete();
	}

	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		// 新建文件输入流并对它进行缓冲
		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);

		// 新建文件输出流并对它进行缓冲
		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		// 缓冲数组
		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		// 刷新此缓冲的输出流
		outBuff.flush();

		// 关闭流
		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
	}

	// 复制文件夹
	public static void copyDirectiory(String sourceDir, String targetDir)
			throws IOException {
		// 新建目标目录
		(new File(targetDir)).mkdirs();
		// 获取源文件夹当前下的文件或目录
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				File sourceFile = file[i];
				// 目标文件
				File targetFile = new File(
						new File(targetDir).getAbsolutePath() + File.separator
								+ file[i].getName());
				copyFile(sourceFile, targetFile);
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + "/" + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = targetDir + "/" + file[i].getName();
				copyDirectiory(dir1, dir2);
			}
		}
	}
}