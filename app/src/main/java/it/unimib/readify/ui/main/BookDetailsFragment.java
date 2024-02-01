package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.OL_COVERS_API_ID_PARAMETER;
import static it.unimib.readify.util.Constants.OL_COVERS_API_IMAGE_SIZE_L;
import static it.unimib.readify.util.Constants.OL_COVERS_API_URL;

import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CommentAdapter;
import it.unimib.readify.databinding.FragmentBookDetailsBinding;
import it.unimib.readify.model.Comment;
import it.unimib.readify.model.OLAuthorApiResponse;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.viewmodel.BookViewModel;


public class BookDetailsFragment extends Fragment {

    private FragmentBookDetailsBinding fragmentBookDetailsBinding;
    private BookViewModel bookViewModel;
    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;



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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentBookDetailsBinding = FragmentBookDetailsBinding.inflate(inflater, container, false);
        return fragmentBookDetailsBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadMenu();
        Bundle arguments = getArguments();
        if (arguments != null) {
            OLWorkApiResponse receivedBook = arguments.getParcelable("book");
            if(receivedBook != null){
                fragmentBookDetailsBinding.bookTitle.setText(receivedBook.getTitle());
                StringBuilder authors = new StringBuilder();
                if(receivedBook.getAuthorList() != null){
                    for(OLAuthorApiResponse author : receivedBook.getAuthorList()){
                        authors.append(author.getName()).append("\n");
                    }
                } else {
                    authors.append(requireContext().getString(R.string.error_author_not_found));
                }
                fragmentBookDetailsBinding.bookAuthor.setText(authors.toString());
                fragmentBookDetailsBinding.bookDescription.setText(receivedBook.getDescription().getValue());
                if(receivedBook.getCovers() != null){
                    int pos = 0;
                    int cover = -1;
                    while (cover == -1 && pos < receivedBook.getCovers().size()) {
                        cover = receivedBook.getCovers().get(pos);
                        pos++;
                    }
                    if (cover == -1) {
                        fragmentBookDetailsBinding.bookImage.setImageResource(R.drawable.image_not_available);
                    } else {
                        RequestOptions requestOptions = new RequestOptions()
                                .placeholder(R.drawable.loading_image_gif)
                                .error(R.drawable.image_not_available);
                        Glide.with(requireActivity().getApplicationContext())
                                .load(OL_COVERS_API_URL + OL_COVERS_API_ID_PARAMETER + cover + OL_COVERS_API_IMAGE_SIZE_L)
                                .apply(requestOptions)
                                .into(fragmentBookDetailsBinding.bookImage);
                    }
                } else {
                    Glide.with(requireActivity().getApplicationContext())
                            .load(R.drawable.image_not_available)
                            .into(fragmentBookDetailsBinding.bookImage);
                }
                // background image blurred
                fragmentBookDetailsBinding.bookBackgroundImage.setImageDrawable(fragmentBookDetailsBinding.bookImage.getDrawable());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    fragmentBookDetailsBinding.bookBackgroundImage.setRenderEffect(RenderEffect.createBlurEffect(70, 70, Shader.TileMode.MIRROR));
                } else {
                   fragmentBookDetailsBinding.bookBackgroundImage.setImageResource(R.drawable.sfondo_13);
                }

                //rating
                if(receivedBook.getRating() != null && receivedBook.getRating().getSummary().getAverage() != 0){
                    float rating = (float) receivedBook.getRating().getSummary().getAverage();
                    fragmentBookDetailsBinding.ratingbarBook.setRating(rating);
                    fragmentBookDetailsBinding.textviewRating.setText(String.format("%s", rating).substring(0,3));
                } else {
                    fragmentBookDetailsBinding.ratingbarBook.setRating(0);
                    fragmentBookDetailsBinding.textviewRating.setText(R.string.rating_not_available);
                }








                //todo sistema, servono chiamate al db + rifare classe comment (quindi modificare anche commentAdapter)
                commentList = fetchComments(receivedBook.getKey());
                RecyclerView recyclerViewSearchResults = fragmentBookDetailsBinding.recyclerviewComments;
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
                commentAdapter = new CommentAdapter(commentList, requireActivity().getApplication(), new CommentAdapter.OnItemClickListener() {
                    @Override
                    public void onCommentClick(Comment comment) {
                        Bundle bundle = new Bundle();
                        // potrebbe servire viewmodel per fare una chiamata al DB e ottenere i dati ottenuti dall'username
                        //todo finisci quando avrremo schermata del profilo

                        //comando di navigation da dettagli a profilo
                    }
                });
                recyclerViewSearchResults.setAdapter(commentAdapter);
                recyclerViewSearchResults.setLayoutManager(layoutManager);
                commentAdapter.notifyItemRangeChanged(0,commentList.size());
            }
        }
    }


    public List<Comment> fetchComments(String key){
        Comment comment1 = new Comment("Username1","Bel libro!",new Date(1609459200000L), 1);
        Comment comment2 = new Comment("Username2","Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at arcu eu velit auctor luctus. Vivamus in lacus vel mauris dignissim fringilla. Integer nec mauris vel nisi fringilla gravida a non augue.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Ut feugiat, odio vitae tristique hendrerit, urna lectus finibus libero, id feugiat neque elit eget urna.\n", new Date(1554076800000L), 1);
        Comment comment3 = new Comment("Username3","Brutto libro, non mi è piaciuto" ,new Date(1491004800000L), 1);
        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);
        comments.add(comment2);
        comments.add(comment3);
        comments.add(comment3);
        comments.add(comment3);
        comments.add(comment3);
        comments.add(comment3);

        // Sort the list by timestamp
        comments.sort(Comparator.comparing(Comment::getDate).reversed());
        return comments;
    }

    public void loadMenu(){
        // Set up the toolbar and remove all icons
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.top_appbar_home);
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                String title = requireContext().getString(R.string.app_name)
                        .concat(" - ")
                        .concat(requireContext().getString(R.string.book_details));
                toolbar.setTitle(title);
                menuInflater.inflate(R.menu.default_appbar_menu, menu);
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        // Enable the back button
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }




}