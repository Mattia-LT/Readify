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
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.adapter.BookRecyclerViewCarouselAdapter;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.repository.BookRepository;
import it.unimib.readify.repository.IBookRepository;
import it.unimib.readify.util.ResponseCallback;

public class HomeFragment extends Fragment implements ResponseCallback {

    private static final boolean USE_NAVIGATION_COMPONENT = true;

    private Button buttonDaRimuovere;

    private RecyclerView recyclerViewTrendingBooks;
    private LinearLayoutManager layoutManager;
    private List<OLWorkApiResponse> bookList;
    private IBookRepository iBookRepository;

    private BookRecyclerViewCarouselAdapter trendingBooksAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        iBookRepository = new BookRepository(requireActivity().getApplication(), this);

        bookList = new ArrayList<>();

        iBookRepository.searchBooks("harry+potter","new",10,0);


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
        trendingBooksAdapter = new BookRecyclerViewCarouselAdapter(bookList, requireActivity().getApplication(), new BookRecyclerViewCarouselAdapter.OnItemClickListener(){
            @Override
            public void onBookItemClick(OLWorkApiResponse book) {
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


    @Override
    public void onSuccess(List<OLWorkApiResponse> bookList) {
    }

    @Override
    public void onWorkSuccess(OLWorkApiResponse work) {
        this.bookList.add(work);
        Log.e("lista",bookList.toString());

        // Notify the adapter about the data change
        trendingBooksAdapter.notifyItemInserted(bookList.size());

    }

    @Override
    public void onFailure(String errorMessage) {
        Log.e("ERRORE", errorMessage);
    }
}