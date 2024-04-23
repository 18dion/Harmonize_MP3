package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class mp3Controller implements Initializable {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ListView<String> listView;

    @FXML
    private ImageView imageView;

    @FXML
    private Button importButton;

    @FXML
    private Button searchButton;

    @FXML
    private Button playlistsButton;

    @FXML
    private Button favoritesButton;

    @FXML
    private Button addPlaylistButton;
    
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialization tasks, if any
    }

    @FXML
    private void handleImportButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import MP3 Files");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 files", "*.mp3"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(importButton.getScene().getWindow());

        if (selectedFiles != null) {
            // Process the selected MP3 files
            importMP3Files(selectedFiles);
        }
    }

    private void importMP3Files(List<File> files) {
        // Assuming only one file is selected for simplicity
        File file = files.get(0);
        String mediaSource = file.toURI().toString();
        Media media = new Media(mediaSource);

        // Create a media player
        mediaPlayer = new MediaPlayer(media);

        // Play the song
        mediaPlayer.play();

        // Update progress bar as the song plays
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                progressBar.setProgress(newValue.toSeconds() / mediaPlayer.getTotalDuration().toSeconds());
            }
        });
    }

    @FXML
    private void handlePlayButtonAction(ActionEvent event) {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.play();
            isPlaying = true;
        }
    }

    @FXML
    private void handlePauseButtonAction(ActionEvent event) {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }
}