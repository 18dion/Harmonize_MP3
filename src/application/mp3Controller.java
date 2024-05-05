package application;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class mp3Controller implements Initializable {

    @FXML
    private ProgressBar progressBar;
    private Timer timer;
    
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
    
    @FXML
    private ImageView playPauseIcon;
    
    @FXML
    private Button songsButton;
    
    @FXML
    private TextField searchTextField;
    
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private List<String> songs = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load songs from storage
        songs = getAllSongsFromLocalStorage("C:\\Users\\Admin\\Downloads");
        listView.getItems().addAll(songs);

        // Add listener to searchTextField
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleSearch(newValue);
            
            progressBar.setOnMousePressed(this::handleProgressBarPressed);
            progressBar.setOnMouseDragged(this::handleProgressBarDragged);
        });
    }

    @FXML
    private void handleShuffleButtonAction(ActionEvent event) {
        shuffleSongs();
    }
    


    
    private void handleProgressBarPressed(MouseEvent event) {
        double mouseX = event.getX();
        double progressBarWidth = progressBar.getWidth();
        double newProgress = (mouseX / progressBarWidth);
        progressBar.setProgress(newProgress);
    }

    private void handleProgressBarDragged(MouseEvent event) {
        double mouseX = event.getX();
        double progressBarWidth = progressBar.getWidth();
        double newProgress = (mouseX / progressBarWidth);
        progressBar.setProgress(newProgress);
    }


 // Method to filter songs based on the entered keyword
    private void handleSearch(String keyword) {
        if (!keyword.isEmpty()) {
            System.out.println("Keyword entered: " + keyword);
            // Filter the songs list
            List<String> filteredSongs = songs.stream()
                    .filter(song -> song.toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
            System.out.println("Filtered songs: " + filteredSongs);
            // Update the list view with filtered songs
            listView.getItems().setAll(filteredSongs);
        } else {
            // If the search keyword is empty, show all songs
            listView.getItems().setAll(songs);
        }
    }


    @FXML
    private void handleSongsButtonAction(ActionEvent event) {
        // Implement logic to fetch all songs from overall local storage
        List<String> songs = getAllSongsFromLocalStorage("C:\\Users\\Admin\\Downloads");

        // Clear the existing items in the ListView
        listView.getItems().clear();

        // Add the fetched songs to the ListView
        listView.getItems().addAll(songs);
    }
    @FXML
    private void handleSongClicked(MouseEvent event) {
        String selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            String filePath = "C:\\Users\\Admin\\Downloads\\" + selectedItem; // Adjust the path accordingly
            Media media = new Media(new File(filePath).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            startProgressBarTimer(); // Start progress bar timer
        }
    }


    private List<String> getAllSongsFromLocalStorage(String directoryPath) {
        List<String> songs = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            songs = paths
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(path -> path.toLowerCase().endsWith(".mp3"))
                    .map(path -> path.substring(path.lastIndexOf(File.separator) + 1))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return songs;
    }

    // Method to shuffle the list of songs
    private void shuffleSongs() {
        ObservableList<String> items = listView.getItems();
        Collections.shuffle(items, new Random());
    }
    

    private void startProgressBarTimer() {
        timer = new Timer();
        progressBar.setMinWidth(150); // Set initial width for the progress bar
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.getStyleClass().add("progress-bar");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING 
                            && mediaPlayer.getTotalDuration() != null) {
                        double currentTime = mediaPlayer.getCurrentTime().toSeconds();
                        double totalTime = mediaPlayer.getTotalDuration().toSeconds();
                        double progress = currentTime / totalTime;
                        progressBar.setProgress(progress);
                        
                        // Update label to show completed time
                        String completedTime = formatTime(currentTime);
                        
                        // Update label to show song length
                        String songLength = formatTime(totalTime);
                        
                        // Set the text on the progress bar
                        String progressBarText = completedTime + " / " + songLength;
                        progressBar.setAccessibleText(progressBarText);
                    }
                });
            }
        }, 0, 100);
    }





    private String formatTime(double seconds) {
        int minutes = (int) seconds / 60;
        int secs = (int) seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    @FXML
    private void handlePlayPauseButtonAction(ActionEvent event) {
        if (mediaPlayer != null) {
            if (isPlaying) {
                mediaPlayer.pause();
                isPlaying = false;
                // Change the button icon to pause
                playPauseIcon.setImage(new Image(getClass().getResourceAsStream("pause.png")));
            } else {
                mediaPlayer.play();
                isPlaying = true;
                // Change the button icon to play
                playPauseIcon.setImage(new Image(getClass().getResourceAsStream("play.png")));
            }
        }
    }
    
    @FXML
    private void handleForwardButtonAction(ActionEvent event) {
        int currentIndex = listView.getSelectionModel().getSelectedIndex();
        if (currentIndex < listView.getItems().size() - 1) {
            listView.getSelectionModel().select(currentIndex + 1);
            handleSongClicked(null); // Play the next song
        }
    }

    @FXML
    private void handleBackwardButtonAction(ActionEvent event) {
        int currentIndex = listView.getSelectionModel().getSelectedIndex();
        if (currentIndex > 0) {
            listView.getSelectionModel().select(currentIndex - 1);
            handleSongClicked(null); // Play the previous song
        }
    }
    
    @FXML
    private void handleRefreshButtonAction(ActionEvent event) {
        // Implement logic to refresh the list view
        List<String> refreshedSongs = getAllSongsFromLocalStorage("C:\\Users\\Admin\\Downloads");
        listView.getItems().clear(); // Clear existing items
        listView.getItems().addAll(refreshedSongs); // Add refreshed songs to the list view
    }



}