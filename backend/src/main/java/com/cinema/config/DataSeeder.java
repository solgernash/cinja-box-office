package com.cinema.config;

import com.cinema.model.Movie;
import com.cinema.repository.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final MovieRepository movieRepository;

    public DataSeeder(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public void run(String... args) {

        Movie movie1 = new Movie(
                null,
                "Shadow Circuit",
                "2h 04m",
                "Action",
                "PG-13",
                "A rogue projectionist uncovers a secret clan hiding clues inside blockbuster trailers.",
                "https://images.unsplash.com/photo-1524985069026-dd778a71c7b4?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "RUNNING",
                List.of("2:00 PM", "5:00 PM", "8:00 PM")
        );
        movieRepository.save(movie1);


        Movie movie2 = new Movie(
                null,
                "Midnight Dojo",
                "1h 51m",
                "Thriller",
                "R",
                "Every seat hides a secret when five strangers enter a theater after closing time.",
                "https://images.unsplash.com/photo-1505686994434-e3cc5abf1330?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "RUNNING",
                List.of("1:30 PM", "6:10 PM", "9:20 PM")
        );
        movieRepository.save(movie2);


        Movie movie3 = new Movie(
                null,
                "Neon Shuriken",
                "2h 15m",
                "Sci-Fi",
                "PG-13",
                "Cyber ninjas race through a glowing city to stop an AI from rewriting every movie ending.",
                "https://images.unsplash.com/photo-1519608487953-e999c86e7455?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "RUNNING",
                List.of("12:45 PM", "4:30 PM", "10:00 PM")
        );
        movieRepository.save(movie3);


        Movie movie4 = new Movie(
                null,
                "Popcorn Ronin",
                "1h 42m",
                "Comedy",
                "PG",
                "A snack-bar worker must defend the last bucket of legendary buttered popcorn.",
                "https://images.unsplash.com/photo-1512149673953-1e251807ec7c?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "RUNNING",
                List.of("11:00 AM", "3:15 PM", "7:45 PM")
        );
        movieRepository.save(movie4);


        Movie movie5 = new Movie(
                null,
                "The Last Ticket",
                "2h 01m",
                "Drama",
                "PG-13",
                "A family reconnects while chasing one sold-out ticket to a once-in-a-lifetime premiere.",
                "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "COMING_SOON",
                List.of("Coming Soon")
        );
        movieRepository.save(movie5);

        Movie movie6 = new Movie(
                null,
                "Cherry Blossom Heist",
                "1h 56m",
                "Adventure",
                "PG",
                "A retired stunt team reunites for one impossible theater vault robbery under falling blossoms.",
                "https://images.unsplash.com/photo-1524231757912-21f4fe3a7200?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "COMING_SOON",
                List.of("Coming Soon")
        );
        movieRepository.save(movie6);


        Movie movie7 = new Movie(
                null,
                "Cosmic Horizon",
                "1h 30m",
                "Sci-Fi",
                "PG-13",
                "A crew ventures beyond known space after discovering a mysterious signal.",
                "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "COMING_SOON",
                List.of("Coming Soon")
        );
        movieRepository.save(movie7);


        Movie movie8 = new Movie(
                null,
                "Laugh Track",
                "2h 10m",
                "Comedy",
                "PG",
                "A struggling comedian accidentally becomes the star of a reality show.",
                "https://plus.unsplash.com/premium_photo-1664882424754-ee3aeaa915cf?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "RUNNING",
                List.of("1:00 PM", "4:00 PM", "7:00 PM")
        );
        movieRepository.save(movie8);


        Movie movie9 = new Movie(
                null,
                "The Silent Witness",
                "2h 15m",
                "Thriller",
                "R",
                "A journalist uncovers a conspiracy after witnessing a crime that no one else believes happened.",
                "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "RUNNING",
                List.of("2:30 PM", "5:30 PM", "8:30 PM")
        );

        movieRepository.save(movie9);


        Movie movie10 = new Movie(
                null,
                "Golden Quest",
                "1h 55m",
                "Adventure",
                "PG",
                "A young explorer searches for a legendary treasure hidden across continents.",
                "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "COMING_SOON",
                List.of("Coming Soon")
        );
        movieRepository.save(movie10);

    } //end run method
} //end class

