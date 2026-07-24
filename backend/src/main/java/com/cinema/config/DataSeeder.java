package com.cinema.config;

import com.cinema.model.*;
import com.cinema.repository.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.ArrayList;
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
    private final BookingRepository bookingRepository;
    private final TheatreRepository theatreRepository;
    private final ShowroomRepository showroomRepository;
    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final TicketRepository ticketRepository;




    //constructor
    public DataSeeder(MovieRepository movieRepository, UserRepository userRepository, FavoriteRepository favoriteRepository, PaymentCardRepository paymentCardRepository, AddressRepository addressRepository, BookingRepository bookingRepository, TheatreRepository theatreRepository, ShowroomRepository showroomRepository, SeatRepository seatRepository, ShowRepository showRepository, ShowSeatRepository showSeatRepository,TicketRepository ticketRepository)
    {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.paymentCardRepository = paymentCardRepository;
        this.addressRepository = addressRepository;
        this.bookingRepository = bookingRepository;
        this.theatreRepository = theatreRepository;
        this.showroomRepository = showroomRepository;
        this.seatRepository = seatRepository;
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.ticketRepository = ticketRepository;
    }


    //runs automatically at application startup
    @Override
    public void run(String... args) {
        // Foundation
        if(theatreRepository.count()==0){

            Theatre theatre = seedTheatre();

            List<Showroom> showrooms = seedShowrooms(theatre);

            seedSeats(showrooms);
        }



        if(movieRepository.count()==0)
            seedMovies();



        if(showRepository.count()==0)
            seedShows();


        if(showSeatRepository.count()==0)
            seedShowSeats();




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


        if(bookingRepository.count()==0)
            seedBookings();

        if (ticketRepository.count() == 0)
            seedTickets();


    } //end run method


    //------------Movie--------------//

    //------------Movie--------------//

    public void seedMovies() {

        Movie movie1 = new Movie(
                null,
                "Shadow Circuit",
                "2h 04m",
                MovieGenre.ACTION,
                8.7,
                "PG-13",
                "A rogue projectionist uncovers a secret clan hiding clues inside blockbuster trailers.",
                "https://images.unsplash.com/photo-1524985069026-dd778a71c7b4?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "RUNNING",
                "Ryan Cooper, Emma Brooks",
                "Christopher Nolan",
                "Warner Bros.",
                "A rogue projectionist uncovers a secret clan hiding clues inside blockbuster trailers.",
                new Date()
        );
        movieRepository.save(movie1);


        Movie movie2 = new Movie(
                null,
                "Midnight Dojo",
                "1h 51m",
                MovieGenre.THRILLER,
                8.2,
                "R",
                "Every seat hides a secret when five strangers enter a theater after closing time.",
                "https://images.unsplash.com/photo-1505686994434-e3cc5abf1330?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "RUNNING",
                "James Carter, Olivia Stone",
                "David Fincher",
                "Universal Pictures",
                "Five strangers uncover terrifying secrets inside an abandoned theater.",
                new Date()
        );
        movieRepository.save(movie2);


        Movie movie3 = new Movie(
                null,
                "Neon Shuriken",
                "2h 15m",
                MovieGenre.SCI_FI,
                9.0,
                "PG-13",
                "Cyber ninjas race through a glowing city to stop an AI from rewriting every movie ending.",
                "https://images.unsplash.com/photo-1519608487953-e999c86e7455?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "RUNNING",
                "Ken Watanabe, Maya Chen",
                "Denis Villeneuve",
                "Legendary Pictures",
                "Cyber ninjas battle a rogue AI threatening the future of cinema.",
                new Date()
        );
        movieRepository.save(movie3);


        Movie movie4 = new Movie(
                null,
                "Popcorn Ronin",
                "1h 42m",
                MovieGenre.COMEDY,
                7.9,
                "PG",
                "A snack-bar worker must defend the last bucket of legendary buttered popcorn.",
                "https://images.unsplash.com/photo-1512149673953-1e251807ec7c?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "RUNNING",
                "Chris Pratt, Kevin Hart",
                "Taika Waititi",
                "Paramount Pictures",
                "An ordinary concession worker becomes an unlikely hero.",
                new Date()
        );
        movieRepository.save(movie4);


        Movie movie5 = new Movie(
                null,
                "The Last Ticket",
                "2h 01m",
                MovieGenre.DRAMA,
                8.4,
                "PG-13",
                "A family reconnects while chasing one sold-out ticket to a once-in-a-lifetime premiere.",
                "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "COMING_SOON",
                "Tom Hanks, Julia Roberts",
                "Steven Spielberg",
                "DreamWorks",
                "A touching family drama centered around one unforgettable movie premiere.",
                new Date()
        );
        movieRepository.save(movie5);


        Movie movie6 = new Movie(
                null,
                "Cherry Blossom Heist",
                "1h 56m",
                MovieGenre.ADVENTURE,
                8.1,
                "PG",
                "A retired stunt team reunites for one impossible theater vault robbery under falling blossoms.",
                "https://images.unsplash.com/photo-1524231757912-21f4fe3a7200?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "COMING_SOON",
                "Jack Black, Scarlett Johansson",
                "Guy Ritchie",
                "Sony Pictures",
                "Retired stunt performers reunite for one final impossible mission.",
                new Date()
        );
        movieRepository.save(movie6);


        Movie movie7 = new Movie(
                null,
                "Cosmic Horizon",
                "1h 30m",
                MovieGenre.SCI_FI,
                8.6,
                "PG-13",
                "A crew ventures beyond known space after discovering a mysterious signal.",
                "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "COMING_SOON",
                "Matt Damon, Zendaya",
                "Ridley Scott",
                "20th Century Studios",
                "A deep-space expedition changes humanity forever.",
                new Date()
        );
        movieRepository.save(movie7);


        Movie movie8 = new Movie(
                null,
                "Laugh Track",
                "2h 10m",
                MovieGenre.COMEDY,
                7.8,
                "PG",
                "A struggling comedian accidentally becomes the star of a reality show.",
                "https://plus.unsplash.com/premium_photo-1664882424754-ee3aeaa915cf?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "RUNNING",
                "Adam Sandler, Tina Fey",
                "Judd Apatow",
                "Netflix Studios",
                "A comedian finds unexpected fame through a bizarre reality TV show.",
                new Date()
        );
        movieRepository.save(movie8);


        Movie movie9 = new Movie(
                null,
                "The Silent Witness",
                "2h 15m",
                MovieGenre.THRILLER,
                8.9,
                "R",
                "A journalist uncovers a conspiracy after witnessing a crime that no one else believes happened.",
                "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "RUNNING",
                "Emily Blunt, Jake Gyllenhaal",
                "Martin Scorsese",
                "Lionsgate",
                "One witness. One conspiracy. No one believes the truth.",
                new Date()
        );
        movieRepository.save(movie9);


        Movie movie10 = new Movie(
                null,
                "Golden Quest",
                "1h 55m",
                MovieGenre.ADVENTURE,
                8.3,
                "PG",
                "A young explorer searches for a legendary treasure hidden across continents.",
                "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=900&q=80",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "COMING_SOON",
                "Chris Hemsworth, Daisy Ridley",
                "Peter Jackson",
                "Disney",
                "An epic journey to discover a legendary lost treasure.",
                new Date()
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
                "456 College Avenue",
                "Athens",
                "Georgia",
                "30605"
        );

        address2.setCustomer(customer2);

        addressRepository.save(address2);
    }

    //--------- booking seeding ---------//

    public void seedBookings() {

        Customer customer = (Customer) userRepository.findByEmail(
                "john.smith@gmail.com"
        );


        PaymentCard card = paymentCardRepository
                .findByCustomerUserId(customer.getUser_ID())
                .get(0);


        Show show = showRepository
                .findAll()
                .get(0);


        Booking booking = new Booking(
                "BOOKING 0001",
                new Date(),
                35.00,
                3.50,
                2.00,
                "PAYMENT REF 0001",
                false
        );


        booking.setCustomer(customer);
        booking.setPaymentCard(card);
        booking.setShow(show);


        bookingRepository.save(booking);
    }

//------------- Theatre Seeding -------------//

    public Theatre seedTheatre() {

        Theatre theatre = new Theatre(
                null,
                "Cinema Palace",
                "Athens, Georgia"
        );

        return theatreRepository.save(theatre);
    }

    //------------- Showroom Seeding -------------//

    public List<Showroom> seedShowrooms(Theatre theatre) {

        Showroom showroom1 = new Showroom(
                null,
                1,
                30,
                theatre
        );


        Showroom showroom2 = new Showroom(
                null,
                2,
                30,
                theatre
        );


        Showroom showroom3 = new Showroom(
                null,
                3,
                30,
                theatre
        );


        return showroomRepository.saveAll(
                List.of(
                        showroom1,
                        showroom2,
                        showroom3
                )
        );
    }

    public void seedSeats(List<Showroom> showrooms) {

        for(Showroom showroom : showrooms){

            for(char row='A'; row<='C'; row++){

                for(int seatNumber=1; seatNumber<=10; seatNumber++){

                    Seat seat = new Seat(
                            null,
                            showroom,
                            String.valueOf(row),
                            seatNumber,
                            SeatType.REGULAR
                    );

                    seatRepository.save(seat);
                }
            }
        }
    }

    //------------- Show Seeding -------------//

    public void seedShows() {

        Movie shadowCircuit =
                movieRepository.findByTitleContainingIgnoreCase("Shadow Circuit")
                        .get(0);


        Movie midnightDojo =
                movieRepository.findByTitleContainingIgnoreCase("Midnight Dojo")
                        .get(0);


        Showroom showroom1 =
                showroomRepository.findByShowroomNumber(1);


        Showroom showroom2 =
                showroomRepository.findByShowroomNumber(2);


        Showroom showroom3 =
                showroomRepository.findByShowroomNumber(3);



        // Showroom 1 - Shadow Circuit 2PM
        Show show1 = new Show(
                null,
                new Date(),
                Time.valueOf("14:00:00"),
                124
        );

        show1.setMovie(shadowCircuit);
        show1.setShowroom(showroom1);
        show1.setTickets(new ArrayList<>());
        show1.setBookings(new ArrayList<>());



        // Showroom 2 - Shadow Circuit 5PM
        Show show2 = new Show(
                null,
                new Date(),
                Time.valueOf("17:00:00"),
                124
        );

        show2.setMovie(shadowCircuit);
        show2.setShowroom(showroom2);
        show2.setTickets(new ArrayList<>());
        show2.setBookings(new ArrayList<>());



        // Showroom 3 - Midnight Dojo 8PM
        Show show3 = new Show(
                null,
                new Date(),
                Time.valueOf("20:00:00"),
                111
        );

        show3.setMovie(midnightDojo);
        show3.setShowroom(showroom3);
        show3.setTickets(new ArrayList<>());
        show3.setBookings(new ArrayList<>());



        showRepository.save(show1);
        showRepository.save(show2);
        showRepository.save(show3);
    }

    public void seedShowSeats() {

        List<Show> shows = showRepository.findAll();

        for(Show show : shows) {

            List<Seat> seats = seatRepository
                    .findByShowroom(show.getShowroom());


            List<ShowSeat> showSeats = new ArrayList<>();


            for(Seat seat : seats) {

                ShowSeat showSeat = new ShowSeat(
                        null,
                        show,
                        seat,
                        true
                );

                showSeats.add(showSeat);
            }


            showSeatRepository.saveAll(showSeats);


            show.setShowSeats(showSeats);

            showRepository.save(show);
        }
    }



    public void seedTickets() {

        Booking booking = bookingRepository.findAll().get(0);

        Show show = booking.getShow();

        List<ShowSeat> showSeats = showSeatRepository.findByShow(show);

        ShowSeat showSeat1 = showSeats.get(0);
        ShowSeat showSeat2 = showSeats.get(1);

        showSeat1.setAvailable(false);
        showSeat2.setAvailable(false);

        showSeatRepository.save(showSeat1);
        showSeatRepository.save(showSeat2);

        Ticket ticket1 = new Ticket(
                "TICKET 0001",
                17.50,
                TicketType.ADULT,
                booking
        );

        ticket1.setShow(show);
        ticket1.setSeat(showSeat1.getSeat());
        ticket1.setShowSeat(showSeat1);

        Ticket ticket2 = new Ticket(
                "TICKET 0002",
                17.50,
                TicketType.ADULT,
                booking
        );

        ticket2.setShow(show);
        ticket2.setSeat(showSeat2.getSeat());
        ticket2.setShowSeat(showSeat2);

        ticketRepository.save(ticket1);
        ticketRepository.save(ticket2);

        booking.setTickets(List.of(ticket1, ticket2));
        bookingRepository.save(booking);

        show.setTickets(List.of(ticket1, ticket2));
        showRepository.save(show);
    }

} //end class

