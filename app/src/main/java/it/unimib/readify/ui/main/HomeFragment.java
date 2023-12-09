package it.unimib.readify.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.adapter.BookRecyclerViewCarouselAdapter;
import it.unimib.readify.model.Book;

public class HomeFragment extends Fragment {

    private static final boolean USE_NAVIGATION_COMPONENT = true;

    private Button buttonDaRimuovere;

    private RecyclerView recyclerViewTrendingBooks;
    private LinearLayoutManager layoutManager;
    private List<Book> bookList;

    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookList = new ArrayList<>();

        bookList.add(new Book("To Kill a Mockingbird", "ISBN-001", getTopics("Fiction", "Legal Drama"), "Harper Lee", "/images/to-kill-a-mockingbird.jpg"));
        bookList.add(new Book("1984", "ISBN-002", getTopics("Dystopian", "Political Fiction"), "George Orwell", "/images/1984.jpg"));
        bookList.add(new Book("The Great Gatsby", "ISBN-003", getTopics("Classic", "Romance"), "F. Scott Fitzgerald", "/images/the-great-gatsby.jpg"));
        bookList.add(new Book("The Catcher in the Rye", "ISBN-004", getTopics("Coming-of-Age", "Drama"), "J.D. Salinger", "/images/the-catcher-in-the-rye.jpg"));
        bookList.add(new Book("Pride and Prejudice", "ISBN-005", getTopics("Classic", "Romance"), "Jane Austen", "/images/pride-and-prejudice.jpg"));
        bookList.add(new Book("The Hobbit", "ISBN-006", getTopics("Fantasy", "Adventure"), "J.R.R. Tolkien", "/images/the-hobbit.jpg"));
        bookList.add(new Book("The Da Vinci Code", "ISBN-007", getTopics("Mystery", "Thriller"), "Dan Brown", "/images/the-da-vinci-code.jpg"));
        bookList.add(new Book("The Harry Potter series", "ISBN-008", getTopics("Fantasy", "Adventure"), "J.K. Rowling", "/images/harry-potter.jpg"));
        bookList.add(new Book("The Lord of the Rings", "ISBN-009", getTopics("Fantasy", "Adventure"), "J.R.R. Tolkien", "/images/the-lord-of-the-rings.jpg"));
        bookList.add(new Book("The Hunger Games", "ISBN-010", getTopics("Dystopian", "Adventure"), "Suzanne Collins", "/images/the-hunger-games.jpg"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //todo controllo

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO da rimuovere
        buttonDaRimuovere = view.findViewById(R.id.button_da_rimuovere);
        buttonDaRimuovere.setOnClickListener(v -> {
            navigateToBookDetailsFragment();
        });


        // RecyclerView for trending carousel
        recyclerViewTrendingBooks = view.findViewById(R.id.trending_container);
        layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);






        // Create an adapter
        BookRecyclerViewCarouselAdapter trendingBooksAdapter = new BookRecyclerViewCarouselAdapter(bookList, requireActivity().getApplication(), new BookRecyclerViewCarouselAdapter.OnItemClickListener(){
            @Override
            public void onBookItemClick(Book book) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("book", book);
                navigateToBookDetailsFragment(bundle);
            }

            @Override
            public void onSaveButtonPressed(int position) {
                //TODO sistemare
                //bookList.get(position).setFavorite(!newsList.get(position).isFavorite());
                //iNewsRepository.updateNews(newsList.get(position));
            }
        });

        // Set the adapter on the RecyclerView
        recyclerViewTrendingBooks.setAdapter(trendingBooksAdapter);
        recyclerViewTrendingBooks.setLayoutManager(layoutManager);





    }

    private void navigateToBookDetailsFragment() {
        if (USE_NAVIGATION_COMPONENT) {
            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_bookDetailsFragment);

        } else {
            Intent intent = new Intent(requireContext(), HomeActivity.class);
            startActivity(intent);
            //requireActivity().finish();
        }
    }

    private void navigateToBookDetailsFragment(Bundle bundle) {
        if (USE_NAVIGATION_COMPONENT) {
            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_bookDetailsFragment, bundle);

        } else {
            Intent intent = new Intent(requireContext(), HomeActivity.class);
            startActivity(intent);
            //requireActivity().finish();
        }
    }



    //TODO da rimuovere, temporanea
    private static ArrayList<String> getTopics(String topic1, String topic2) {
        ArrayList<String> topics = new ArrayList<>();
        topics.add(topic1);
        topics.add(topic2);
        return topics;
    }


}