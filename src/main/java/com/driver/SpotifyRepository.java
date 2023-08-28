package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;


    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();


        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        return user;

}
    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    // 3 rd API start
    public Album createAlbum(String title, String artistName) {
        Artist artist = null;
        for (Artist existingArtist : artists) {
            if (existingArtist.getName().equals(artistName)) {
                artist = existingArtist;
                break;
            }
        }

        if (artist == null) {
            artist = new Artist(artistName);
            artists.add(artist);
        }

        Album album = new Album(title);
        albums.add(album);

        associateAlbumWithArtist(album, artist);
        return album;

    }

    private void associateAlbumWithArtist(Album album, Artist artist) {
        if(artistAlbumMap.containsKey(artist))
        {
            artistAlbumMap.get(artist).add(album);
        }
        else
        {
            ArrayList<Album>albumList=new ArrayList<>();
            albumList.add(album);
            artistAlbumMap.put(artist,albumList);
        }
    }

    // 3 rd Api End
    // 4th API START
    public Song createSong(String title, String albumName, int length) throws Exception{
            Album album=findAlbumByName(albumName);
            if(album==null)
            {
                throw new Exception("Album does not exist");
            }
        Song song = new Song(title, length);
        songs.add(song);
        associateSongWithAlbum(album, song);
        return song;
        }

    private void associateSongWithAlbum(Album album, Song song) {
        if(albumSongMap.containsKey(album))
        {
            albumSongMap.get(album).add(song);
        }
        else
        {
            ArrayList<Song>songsList=new ArrayList<>();
            songsList.add(song);
            albumSongMap.put(album,songsList);
        }
    }

    private Album findAlbumByName(String albumName) {
        Album album=null;
        for(Album existingAlbum:albums)
        {
            if(existingAlbum.getTitle().equals(albumName))
            {
                album=existingAlbum;
                return album;
            }
        }
        return album;

    }
    // API END
    // 5 th API
    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user = getUserByMobile(mobile);
        if (user == null) {
            throw new Exception("User does not exist");
        }

        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        associateUserWithPlaylist(user, playlist);
        creatorPlaylistMapSet(user,playlist);
        playlistListenerMapSet(user,playlist);
        for (Map.Entry<Album, List<Song>> entry : albumSongMap.entrySet()) {
            for (Song song : entry.getValue()) {
                if (song.getLength() == length) {
                    associateSongWithPlaylist(playlist, song);
                }
            }
        }
        return playlist;

    }

    private void playlistListenerMapSet(User user, Playlist playlist) {

        if(playlistListenerMap.containsKey(playlist))
        {
            playlistListenerMap.get(playlist).add(user);
        }
        else
        {
            List<User>userList=new ArrayList<>();
            userList.add(user);
            playlistListenerMap.put(playlist,userList);
        }
    }

    private void creatorPlaylistMapSet(User user, Playlist playlist) {
        if(!creatorPlaylistMap.containsKey(user))
        {
            creatorPlaylistMap.put(user,playlist);
        }
    }

    private void associateSongWithPlaylist(Playlist playlist, Song song) {
        if(playlistSongMap.containsKey(playlist))
        {
            playlistSongMap.get(playlist).add(song);
        }
        else
        {
            ArrayList<Song>songs1=new ArrayList<>();
            songs1.add(song);
            playlistSongMap.put(playlist,songs1);
        }
    }

    private void associateUserWithPlaylist(User user, Playlist playlist) {
        if(userPlaylistMap.containsKey(user))
        {
            userPlaylistMap.get(user).add(playlist);
        }
        else
        {
            ArrayList<Playlist> playlists1=new ArrayList<>();
            playlists1.add(playlist);
            userPlaylistMap.put(user,playlists1);
        }
    }

    private User getUserByMobile(String mobile) {
        User user=null;
        for(User currentUser:users)
        {
            if(currentUser.getMobile().equals(mobile))
            {
                user=currentUser;
                return user;
            }
        }
        return user;
    }
    // 5 th API End.
    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = getUserByMobile(mobile);
        if (user == null) {
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        associateUserWithPlaylist(user, playlist);
        creatorPlaylistMapSet(user,playlist);
        playlistListenerMapSet(user,playlist);
        for (String songTitle : songTitles) {
            Song song = getSongByTitle(songTitle);
            if (song != null) {
                associateSongWithPlaylist(playlist, song);
            }
        }

        return playlist;

    }

    private Song getSongByTitle(String songTitle) {
        Song song=null;
        for(Song currentSong:songs)
        {
            if(song.getTitle().equals(songTitle))
            {
                song=currentSong;
                return song;
            }
        }
        return song;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = getUserByMobile(mobile);
        if (user == null) {
            throw new Exception("User does not exist");
        }

        Playlist playlist = null;
        for (Playlist existingPlaylist : playlists) {
            if (existingPlaylist.getTitle().equals(playlistTitle)) {
                playlist = existingPlaylist;
                break;
            }
        }

        if (playlist == null) {
            throw new Exception("Playlist does not exist");
        }

        List<User> listeners = playlistListenerMap.getOrDefault(playlist, new ArrayList<>());
        if (!listeners.contains(user)) {
            listeners.add(user);
            playlistListenerMap.put(playlist, listeners);
        }

        return playlist;

    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = getUserByMobile(mobile);
        if (user == null) {
            throw new Exception("User does not exist");
        }

        Song song = getSongByTitle(songTitle);
        if (song == null) {
            throw new Exception("Song does not exist");
        }

        if (songLikeMap.containsKey(song)) {
            List<User> likers = songLikeMap.get(song);
            if (!likers.contains(user)) {
                likers.add(user);
                songLikeMap.put(song, likers);
                // Auto-like the corresponding artist
                Artist artist = getArtistForSong(song);
                artist.setLikes(artist.getLikes()+1);
            }
        } else {
            List<User> likers = new ArrayList<>();
            likers.add(user);
            songLikeMap.put(song, likers);
            Artist artist = getArtistForSong(song);
            artist.setLikes(artist.getLikes()+1);
        }

        return song;

    }

    private Artist getArtistForSong(Song song) {
        for (Map.Entry<Album, List<Song>> entry : albumSongMap.entrySet()) {
            if (entry.getValue().contains(song)) {
                Album album = entry.getKey();
                for (Map.Entry<Artist, List<Album>> artistEntry : artistAlbumMap.entrySet()) {
                    if (artistEntry.getValue().contains(album)) {
                        return artistEntry.getKey(); // Return the artist associated with the album containing the song
                    }
                }
            }
        }
        return null;

    }



    public String mostPopularArtist() {
        Artist mostPopularArtist = null;
        int maxLikes = 0;

        for (Artist artist:artists) {

            int numLikes = artist.getLikes();
            if (numLikes > maxLikes) {
                maxLikes = numLikes;
                mostPopularArtist = artist;
            }
        }

        if (mostPopularArtist != null) {
            return mostPopularArtist.getName();
        } else {
            return "No artists with likes yet";
        }
    }

    public String mostPopularSong() {
        Song mostPopularSong = null;
        int maxLikes = 0;

        for (Map.Entry<Song, List<User>> entry : songLikeMap.entrySet()) {
            int numLikes = entry.getValue().size();
            if (numLikes > maxLikes) {
                maxLikes = numLikes;
                mostPopularSong = entry.getKey();
            }
        }

        if (mostPopularSong != null) {
            return mostPopularSong.getTitle();
        } else {
            return "No songs with likes yet";
        }
    }
}
