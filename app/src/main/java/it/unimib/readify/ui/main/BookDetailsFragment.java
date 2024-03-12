package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.BUNDLE_BOOK;
import static it.unimib.readify.util.Constants.BUNDLE_USER;
import static it.unimib.readify.util.Constants.OL_COVERS_API_ID_PARAMETER;
import static it.unimib.readify.util.Constants.OL_COVERS_API_IMAGE_SIZE_L;
import static it.unimib.readify.util.Constants.OL_COVERS_API_URL;

import android.graphics.PorterDuff;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CommentAdapter;
import it.unimib.readify.data.repository.user.TestIDatabaseRepository;
import it.unimib.readify.databinding.FragmentBookDetailsBinding;
import it.unimib.readify.model.Comment;
import it.unimib.readify.model.OLAuthorApiResponse;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.util.TestServiceLocator;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;


public class BookDetailsFragment extends Fragment {

    private FragmentBookDetailsBinding fragmentBookDetailsBinding;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private TestDatabaseViewModel testDatabaseViewModel;
    //todo rimuovi user quando cambi logica login
    private User user;
    public BookDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
        initObserver();
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
        fetchBookData();
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
        Drawable coloredIcon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_24);
        int newColor = getResources().getColor(R.color.white, null);
        if (coloredIcon != null) {
            coloredIcon.setColorFilter(newColor, PorterDuff.Mode.SRC_IN);
        }
        toolbar.setNavigationIcon(coloredIcon);
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

    }

    private void fetchBookData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            OLWorkApiResponse receivedBook = arguments.getParcelable(BUNDLE_BOOK);
            if (receivedBook != null) {
                loadAuthors(receivedBook);
                loadCover(receivedBook);
                loadRating(receivedBook);
                loadComments(receivedBook);
                loadAddCommentSection(receivedBook);
                fragmentBookDetailsBinding.bookDescription.setText(receivedBook.getDescription().getValue());
            }
        }
    }

    private void initViewModels(){
        TestIDatabaseRepository testIDatabaseRepository = TestServiceLocator
                .getInstance(requireActivity().getApplication())
                .getRepository(TestIDatabaseRepository.class);
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(testIDatabaseRepository)
                .create(TestDatabaseViewModel.class);
    }

    private void loadRating(OLWorkApiResponse book){
        if(book.getRating() != null && book.getRating().getSummary().getAverage() != 0){
            float rating = (float) book.getRating().getSummary().getAverage();
            fragmentBookDetailsBinding.ratingbarBook.setRating(rating);
            String ratingString = String.format("%s", rating).substring(0,3)
                    .concat(" (")
                    .concat(String.format("%s", book.getRating().getSummary().getCount()))
                    .concat(")");
            fragmentBookDetailsBinding.textviewRating.setText(ratingString);
        } else {
            fragmentBookDetailsBinding.ratingbarBook.setRating(0);
            fragmentBookDetailsBinding.textviewRating.setText(R.string.rating_not_available);
        }
    }

    private void loadAuthors(OLWorkApiResponse book){
        fragmentBookDetailsBinding.bookTitle.setText(book.getTitle());
        StringBuilder authors = new StringBuilder();
        if(book.getAuthorList() != null){
            for(OLAuthorApiResponse author : book.getAuthorList()){
                authors.append(author.getName()).append("\n");
            }
        } else {
            authors.append(requireContext().getString(R.string.error_author_not_found));
        }
        fragmentBookDetailsBinding.bookAuthor.setText(authors.toString());
    }

    private void loadCover(OLWorkApiResponse book){
        //TODO should change loading image maybe
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.loading_image_gif)
                .error(R.drawable.image_not_available);

        if(book.getCovers() != null){
            int pos = 0;
            int cover = -1;
            while (cover == -1 && pos < book.getCovers().size()) {
                cover = book.getCovers().get(pos);
                pos++;
            }

            if (cover == -1) {
                fragmentBookDetailsBinding.bookImage.setImageResource(R.drawable.image_not_available);
            } else {
                Glide.with(requireActivity().getApplicationContext())
                        .load(OL_COVERS_API_URL + OL_COVERS_API_ID_PARAMETER + cover + OL_COVERS_API_IMAGE_SIZE_L)
                        .apply(requestOptions)
                        .into(fragmentBookDetailsBinding.bookImage);
                Glide.with(requireActivity().getApplicationContext())
                        .load(OL_COVERS_API_URL + OL_COVERS_API_ID_PARAMETER + cover + OL_COVERS_API_IMAGE_SIZE_L)
                        .apply(requestOptions)
                        .into(fragmentBookDetailsBinding.bookBackgroundImage);
            }
        } else {
            Glide.with(requireActivity().getApplicationContext())
                    .load(R.drawable.image_not_available)
                    .apply(requestOptions)
                    .into(fragmentBookDetailsBinding.bookImage);
        }
        // background image blurred
        fragmentBookDetailsBinding.bookBackgroundImage.setImageDrawable(fragmentBookDetailsBinding.bookImage.getDrawable());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            fragmentBookDetailsBinding.bookBackgroundImage.setRenderEffect(RenderEffect.createBlurEffect(70, 70, Shader.TileMode.MIRROR));
        } else {
            fragmentBookDetailsBinding.bookBackgroundImage.setImageResource(R.drawable.sfondo_13);
        }
    }

    private void loadComments(OLWorkApiResponse book){
        RecyclerView recyclerViewSearchResults = fragmentBookDetailsBinding.recyclerviewComments;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());

        commentAdapter = new CommentAdapter(commentList, requireActivity().getApplication(), comment -> {
            if (comment != null && comment.getIdToken() != null) {
                Log.d("Fragment", "Comment username:" + comment.getIdToken());
                Bundle bundle = new Bundle();
                bundle.putParcelable(BUNDLE_USER, comment.getUser());
                Navigation.findNavController(requireView()).navigate(R.id.action_bookDetailsFragment_to_userDetailsFragment, bundle);
            } else {
                Log.d("Fragment", "Error - comment is null OR userId is null");
                // todo handle the error
            }
        });

        Log.d("BookDetails Fragment", "Fetch comments start : " + book.getKey());
        recyclerViewSearchResults.setAdapter(commentAdapter);
        recyclerViewSearchResults.setLayoutManager(layoutManager);
        testDatabaseViewModel.fetchComments(book.getKey());
    }

    private void loadAddCommentSection(OLWorkApiResponse book){
        fragmentBookDetailsBinding.buttonAddComment.setOnClickListener(v -> {
            Editable text = fragmentBookDetailsBinding.edittextComment.getText();
            String commentContent = (text != null) ? text.toString() : "";
            commentContent = commentContent.trim();
            Snackbar.make(requireView(), "commentContent: " + commentContent, Snackbar.LENGTH_SHORT).show();
            if(commentContent.isEmpty()){
                //todo cambia stringa
                Snackbar.make(requireView(), getString(R.string.empty_search_snackbar), Snackbar.LENGTH_SHORT).show();
            } else {
                //TODO rivedi logica login, ho una idea
                String bookId = book.getKey();
                //String idToken = user.getIdToken();
                String idToken = "15yxdq8s6nM3y5LQ8kTkL94D2MC3";
                testDatabaseViewModel.addComment(commentContent,bookId,idToken);
                text.clear();
            }
        });
    }

    private void initObserver(){
        this.commentList = new ArrayList<>();

        final Observer<List<Result>> commentListObserver = results -> {
            List<Comment> commentResultList = results.stream()
                    .filter(result -> result instanceof Result.CommentSuccess)
                    .map(result -> ((Result.CommentSuccess) result).getData())
                    .collect(Collectors.toList());
            Log.d("BookDetails Fragment", "Comment list : " + commentResultList);
            commentResultList.sort(Comparator.comparing(Comment::getTimestamp));
            commentList = commentResultList;
            commentAdapter.refreshList(commentResultList);
        };
        testDatabaseViewModel.getCommentList().observe(this, commentListObserver);

        //TODO CAMBIA LOGICA DIETRO A QUESTO
        final Observer<Result> loggedUserObserver = result -> {
            Log.d("profile fragment", "user changed");
            if(result.isSuccess()) {
                this.user = ((Result.UserSuccess) result).getData();
            }
        };
        //get user data from database
        testDatabaseViewModel.getUserMediatorLiveData().observe(this, loggedUserObserver);
    }


}