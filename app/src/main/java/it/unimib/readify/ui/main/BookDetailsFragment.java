package it.unimib.readify.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CommentAdapter;
import it.unimib.readify.model.Comment;
import it.unimib.readify.model.OLAuthorApiResponse;
import it.unimib.readify.model.OLWorkApiResponse;

public class BookDetailsFragment extends Fragment {

    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    private TextView textViewBookTitle;
    private TextView textViewBookAuthor;
    private TextView textViewBookDescription;



    public BookDetailsFragment() {
        // Required empty public constructor
    }

    public static BookDetailsFragment newInstance() {
        return new BookDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_book_details, container, false);

        textViewBookTitle = rootView.findViewById(R.id.book_title);
        textViewBookAuthor = rootView.findViewById(R.id.book_author);
        textViewBookDescription = rootView.findViewById(R.id.book_description);

        Bundle arguments = getArguments();
        if (arguments != null) {
            OLWorkApiResponse receivedBook = arguments.getParcelable("book");
            if(receivedBook != null){
                textViewBookTitle.setText(receivedBook.getTitle());
                StringBuilder authors = new StringBuilder();
                for(OLAuthorApiResponse author : receivedBook.getAuthorList()){
                    authors.append(author.getName()).append("\n");
                }
                textViewBookAuthor.setText(authors.toString());
                textViewBookDescription.setText(receivedBook.getDescription().getValue());
            }

        }

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




    }


}