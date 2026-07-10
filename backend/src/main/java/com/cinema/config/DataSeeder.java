package com.cinema.config;

import com.cinema.model.AccountState;
import com.cinema.model.Address;
import com.cinema.model.Admin;
import com.cinema.model.Customer;
import com.cinema.model.Favorite;
import com.cinema.model.Movie;
import com.cinema.model.PaymentCard;
import com.cinema.repository.AddressRepository;
import com.cinema.repository.FavoriteRepository;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.PaymentCardRepository;
import com.cinema.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/*
 * @Component -> makes this class Spring-managed
 * CommandLineRunner -> runs this code automatically when the Spring Boot app starts
 * Purpose: Seed the database with sample data
 */
@Component
public class DataSeeder implements CommandLineRunner {


    //fields
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final AddressRepository addressRepository;





    //constructor
    public DataSeeder(MovieRepository movieRepository, UserRepository userRepository, FavoriteRepository favoriteRepository, PaymentCardRepository paymentCardRepository, AddressRepository addressRepository)
    {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.paymentCardRepository = paymentCardRepository;
        this.addressRepository = addressRepository;
    }


    //runs automatically at application startup
    @Override
    public void run(String... args) {
        if(movieRepository.count()==0)
            seedMovies();

        if(userRepository.count()==0) {
            seedCustomer();
            seedAdmin();
        }


        if(favoriteRepository.count()==0)
            seedFavorites();

        if(paymentCardRepository.count()==0)
            seedPaymentCards();

        if(addressRepository.count()==0)
            seedAddresses();

    } //end run method


    //------------Movie--------------//

    public void seedMovies() {
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
    }

    //------------- Admin Seeding -------------//

    public void seedAdmin() {
        Admin admin1 = new Admin("ADMIN 0001", "System", "Administrator", "admin1@cinema.com", "Admin1123!");
        Admin admin2 = new Admin("ADMIN 0002", "Movie", "Manager", "admin2@cinema.com", "Admin2123!");
        userRepository.save(admin1);
        userRepository.save(admin2);
    }

    //------------ Customer Seeding -------------//
     public void seedCustomer() {
         // Verified customer with favorite movie
         Customer customer1 = new Customer(
                 "USER 0001",
                 "John",
                 "Smith",
                 "john.smith@gmail.com",
                 "Password123!",
                 AccountState.ACTIVE
         );


         // Verified customer with 3 payment cards
         Customer customer2 = new Customer(
                 "USER 0002",
                 "Sarah",
                 "Johnson",
                 "sarah.johnson@gmail.com",
                 "Password456!",
                 AccountState.ACTIVE
         );


         userRepository.save(customer1);
         userRepository.save(customer2);
     }


     //----------favorite seeding-----------
     public void seedFavorites() {

         Customer customer = (Customer) userRepository.findByEmail("john.smith@gmail.com");
         Movie movie = movieRepository.findByTitleContainingIgnoreCase("Shadow Circuit")
                 .get(0);


         Favorite favorite = new Favorite(new Date());

         favorite.setCustomer(customer);
         favorite.setMovie(movie);


         favoriteRepository.save(favorite);
     }

    //---------payment cards seeding -----------//
    //---------payment cards seeding -----------//
    public void seedPaymentCards() {

        Customer customer = (Customer) userRepository.findByEmail("sarah.johnson@gmail.com");


        PaymentCard card1 = new PaymentCard(
                null,
                "4111111111111111",
                "Sarah Johnson",
                "12/28"
        );

        card1.setCustomer(customer);


        PaymentCard card2 = new PaymentCard(
                null,
                "5555555555554444",
                "Sarah Johnson",
                "06/29"
        );

        card2.setCustomer(customer);


        PaymentCard card3 = new PaymentCard(
                null,
                "378282246310005",
                "Sarah Johnson",
                "09/30"
        );

        card3.setCustomer(customer);


        paymentCardRepository.save(card1);
        paymentCardRepository.save(card2);
        paymentCardRepository.save(card3);
    }

    //--------- addresses seeding -------------//
    public void seedAddresses() {

        Customer customer1 = (Customer) userRepository.findByEmail("john.smith@gmail.com");


        Address address1 = new Address(
                null,
                "123 Main Street",
                "Athens",
                "Georgia",
                "30601"
        );


        address1.setCustomer(customer1);


        addressRepository.save(address1);

        Customer customer2 = (Customer) userRepository.findByEmail("sarah.johnson@gmail.com");

        Address address2 = new Address(
                null,
                "Downtown Atl",
                "Atlanta",
                "Georgia",
                "306501"
        );


        address2.setCustomer(customer2);


        addressRepository.save(address2);
    }




} //end class

