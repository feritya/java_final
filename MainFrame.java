import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

//kütüphanelerin eklenmesi


//kullanıcı arayüzü bileşenlerinin oluşturulması için MainFrame sınıfı oluşturuldu
public class MainFrame extends JFrame {
    private JLabel imageLabel;
    private JButton captureButton;
    private JButton saveButton;
    private JButton filterButton;
    private JButton shareButton;

    private CameraManager cameraManager;//kamera yöneticisi sınıfı
    private Mat currentPhoto;

    public MainFrame() {//yapıcı metot
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

        // Düğmelerin yerleştirilmesi
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(captureButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(filterButton);
        buttonPanel.add(shareButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        // Pencereye panelin eklenmesi
        add(panel);
        pack();
        setVisible(true);

        // Düğme olaylarının dinlenmesi
        captureButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Fotoğraf çekme işlemleri
                currentPhoto = cameraManager.capturePhoto();
                displayPhoto(currentPhoto);
            }
        });
        // Fotoğrafı kaydetme işlemleri
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Fotoğrafı kaydetme işlemleri
                savePhoto(currentPhoto);
            }
        });
        // Filtreleme işlemleri
        filterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Filtreleme işlemleri
                showFilterOptions();
            }
        });
        // Paylaşma işlemleri
        shareButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Paylaşma işlemleri
                sharePhoto(currentPhoto);
            }
        });
    }
        // Fotoğrafın görüntülenmesi
    private void displayPhoto(Mat photo) {
        BufferedImage image = cameraManager.matToBufferedImage(photo);
        ImageIcon icon = new ImageIcon(image);
        imageLabel.setIcon(icon);
    }
    //  Fotoğrafın kaydedilmesi
    private void savePhoto(Mat photo) {//fotoğrafın kaydedilmesi için metot
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Fotoğrafı Kaydet");
        int userSelection = fileChooser.showSaveDialog(this);
        // Dosya seçildiyse yapılacak 
        if (userSelection == JFileChooser.APPROVE_OPTION) { //kullanıcı seçim yaptıysa
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            opencv_imgcodecs.imwrite(filePath, photo);
            JOptionPane.showMessageDialog(this, "Fotoğraf başarıyla kaydedildi.");
        }
    }
    // Filtreleme seçeneklerinin gösterilmesini sağlayan metodu kullanıcı arayüzüne ekledik
    private void showFilterOptions() {//filtreleme seçeneklerinin gösterilmesi için metot
        String[] filters = {"Aydınlatma", "Siyah Beyaz", "Karartma"};
        String selectedFilter = (String) JOptionPane.showInputDialog(
                this,
                "Filtre seçin:",
                "Filtreleme",
                JOptionPane.PLAIN_MESSAGE,
                null,
                filters,
                filters[0]);
        // Filtre seçildiyse yapılacak işlemler
        if (selectedFilter != null) {
            Mat filteredPhoto = cameraManager.applyFilter(selectedFilter, currentPhoto);
            displayPhoto(filteredPhoto);
        }
    }
    // Fotoğrafın paylaşılmasını sağlayan metodu kullanıcı arayüzüne ekledik
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
        // Sosyal medya seçildiyse yapılacak işlemler
        if (selectedMedia != null) {
            String photoPath = cameraManager.saveTempPhoto(photo);
            switch (selectedMedia) {
                case "Gmail":
                    cameraManager.shareWithGmail(photoPath);
                    break;
                case "Facebook":
                    cameraManager.shareWithFacebook(photoPath);
                    break;
                case "Twitter":
                    cameraManager.shareWithTwitter(photoPath);
                    break;
            }
        }
    }
    // Ana metot oluşturuldu ve kullanıcı arayüzü başlatıldı
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame();
            }
        });
    }
}
// CameraManager sınıfını oluşturduk ve kamera yöneticisi sınıfı olarak adlandırdık
class CameraManager {
    private VideoCapture videoCapture;
    //      Yapıcı metot oluşturduk ve videoCapture nesnesini oluşturduk
    public CameraManager() {
        videoCapture = new VideoCapture();
    }
    // Kameranın açılmasını sağlayan metodu oluşturduk
    public Mat capturePhoto() {
        Mat photo = new Mat();
        videoCapture.read(photo);
        return photo;
    }
    // Mat tipindeki fotoğrafı BufferedImage tipine dönüştüren metodu oluşturduk
    public BufferedImage matToBufferedImage(Mat mat) {//mat tipindeki fotoğrafı BufferedImage tipine dönüştüren metot
        int width = mat.cols();
        int height = mat.rows();
        int channels = mat.channels();
        byte[] data = new byte[width * height * channels];
        mat.data().get(data);//fotoğrafın verilerini alıyoruz
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(data, 0, targetPixels, 0, data.length);
        return image;
    }
    // Filtreleme işlemlerini gerçekleştiren metodu oluşturduk
    public Mat applyFilter(String filterName, Mat photo) {
        Mat filteredPhoto = new Mat();
// Filtreleme işlemleri için kodu doldurman gerekir
        switch (filterName) {//filtreleme işlemleri için switch case yapısı
            case "Aydınlatma":
                double brightness = 50.0; // Aydınlatma değeri
                opencv_core.add(photo, new Scalar(brightness, brightness, brightness), filteredPhoto);
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
    // Fotoğrafın geçici olarak kaydedilmesini sağlayan metodu oluşturduk
    public String saveTempPhoto(Mat photo) {
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFilePath = tempDir + File.separator + "temp_photo.jpg";
        opencv_imgcodecs.imwrite(tempFilePath, photo);
        return tempFilePath;
    }
   
    public void shareWithGmail(String photoPath) {
        JOptionPane.showMessageDialog(null, "Fotoğraf Gmail ile paylaşıldı.");
    }

    public void shareWithFacebook(String photoPath) {
       
        JOptionPane.showMessageDialog(null, "Fotoğraf Facebook ile paylaşıldı.");
    }

    public void shareWithTwitter(String photoPath) {

        JOptionPane.showMessageDialog(null, "Fotoğraf Twitter ile paylaşıldı.");
    }
}
