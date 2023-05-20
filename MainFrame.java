import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.imgproc.Imgproc;
 
public class MainFrame extends JFrame {
    private JLabel imageLabel;
    private JButton captureButton;
    private JButton saveButton;
    private JButton filterButton;
    private JButton shareButton;

    private CameraManager cameraManager;
    private Mat currentPhoto;

    public MainFrame() {
        setTitle("Fotoğraf Uygulaması");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 400));

        cameraManager = new CameraManager();

        // Kullanıcı arayüzü bileşenlerinin oluşturulması
        imageLabel = new JLabel();
        captureButton = new JButton("Fotoğraf Çek");
        saveButton = new JButton("Kaydet");
        filterButton = new JButton("Filtrele");
        shareButton = new JButton("Paylaş");

        // Bileşenlerin yerleştirilmesi
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(imageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(captureButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(filterButton);
        buttonPanel.add(shareButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
        pack();
        setVisible(true);

        // Düğme olaylarının dinlenmesi
        captureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Fotoğraf çekme işlemleri
                currentPhoto = cameraManager.capturePhoto();
                displayPhoto(currentPhoto);
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Fotoğrafı kaydetme işlemleri
                savePhoto(currentPhoto);
            }
        });

        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Filtreleme işlemleri
                showFilterOptions();
            }
        });

        shareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Paylaşma işlemleri
                sharePhoto(currentPhoto);
            }
        });
    }

    private void displayPhoto(org.bytedeco.opencv.opencv_core.Mat photo) {
        BufferedImage image = matToBufferedImage(photo);
        ImageIcon icon = new ImageIcon(image);
        imageLabel.setIcon(icon);
    }

    private void savePhoto(Mat photo) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Fotoğrafı Kaydet");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            opencv_imgcodecs.imwrite(filePath, photo);
            JOptionPane.showMessageDialog(this, "Fotoğraf başarıyla kaydedildi.");
        }
    }

    private void showFilterOptions() {
        String[] filters = {"Aydınlatma", "Siyah Beyaz", "Karartma"};
        String selectedFilter = (String) JOptionPane.showInputDialog(
                this,
                "Filtre seçin:",
                "Filtreleme",
                JOptionPane.PLAIN_MESSAGE,
                null,
                filters,
                filters[0]);

        if (selectedFilter != null) {
            Mat filteredPhoto = applyFilter(selectedFilter, currentPhoto);
            displayPhoto(filteredPhoto);
        }
    }

    private Mat applyFilter(String filterName, Mat photo) {
        Mat filteredPhoto = new Mat();

        switch (filterName) {
            case "Aydınlatma":
                double brightness = 50.0; // Aydınlatma değeri
                Mat ımage = new Mat();
                Mat filteredPhotoMat= new Mat();
                photo.convertTo(filteredPhoto, -1, 1, brightness);

                // opencv_core.add(photo,  Scalar(brightness,brightness,brightness), filteredPhoto);
                break;
            case "Siyah Beyaz":
            opencv_core.cvtColor(photo, filteredPhoto, opencv_core.COLOR_BGR2GRAY);

                break;
            case "Karartma":
                double intensity = 50.0; // Karartma değeri
                opencv_core.subtract(photo, new Scalar(intensity, intensity, intensity), filteredPhoto);
                break;
        }

        return filteredPhoto;
    }

    private void sharePhoto(Mat photo) {
        String[] socialMedia = {"Gmail", "Facebook", "Twitter"};
        String selectedMedia = (String) JOptionPane.showInputDialog(
                this,
                "Paylaşmak istediğiniz sosyal medya seçin:",
                "Paylaş",
                JOptionPane.PLAIN_MESSAGE,
                null,
                socialMedia,
                socialMedia[0]);

        if (selectedMedia != null) {
            String photoPath = saveTempPhoto(photo);
            switch (selectedMedia) {
                case "Gmail":
                    shareWithGmail(photoPath);
                    break;
                case "Facebook":
                    shareWithFacebook(photoPath);
                    break;
                case "Twitter":
                    shareWithTwitter(photoPath);
                    break;
            }
        }
    }

    private String saveTempPhoto(Mat photo) {
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFilePath = tempDir + File.separator + "temp_photo.jpg";
        opencv_imgcodecs.imwrite(tempFilePath, photo);
        return tempFilePath;
    }

    private void shareWithGmail(String photoPath) {
        // TODO: Gmail ile fotoğrafın paylaşılması
        JOptionPane.showMessageDialog(this, "Fotoğraf Gmail ile paylaşıldı.");
    }

    private void shareWithFacebook(String photoPath) {
        // TODO: Facebook ile fotoğrafın paylaşılması
        JOptionPane.showMessageDialog(this, "Fotoğraf Facebook ile paylaşıldı.");
    }

    private void shareWithTwitter(String photoPath) {
        // TODO: Twitter ile fotoğrafın paylaşılması
        JOptionPane.showMessageDialog(this, "Fotoğraf Twitter ile paylaşıldı.");
    }

    private BufferedImage matToBufferedImage(org.opencv.core.Mat mat) {
      

        int width = mat.cols();
        int height = mat.rows();
        int channels = mat.channels();

        byte[] data = new byte[width * height * channels];
        mat.get(0, 0, data);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        image.getRaster().setDataElements(0, 0, width, height, data);

        return image;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame();
            }
        });
    }
}
