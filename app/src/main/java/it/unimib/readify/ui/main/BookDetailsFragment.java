package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.DESCRIPTION_TRIM_OPTIONS;
import static it.unimib.readify.util.Constants.OL_COVERS_API_ID_PARAMETER;
import static it.unimib.readify.util.Constants.OL_COVERS_API_IMAGE_SIZE_L;
import static it.unimib.readify.util.Constants.OL_COVERS_API_URL;

import android.content.Context;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CommentAdapter;
import it.unimib.readify.databinding.FragmentBookDetailsBinding;
import it.unimib.readify.model.Comment;
import it.unimib.readify.model.OLAuthorApiResponse;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.CustomViewModelFactory;
import it.unimib.readify.viewmodel.UserViewModel;


public class BookDetailsFragment extends Fragment {

    private FragmentBookDetailsBinding fragmentBookDetailsBinding;
    private CommentAdapter commentAdapter;
    private UserViewModel userViewModel;
    private OLWorkApiResponse receivedBook;
    private User user;
    public BookDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
        initViewModels();
        initObserver();
        showBookInfo();
    }

    private void showBookInfo() {
        receivedBook =  BookDetailsFragmentArgs.fromBundle(getArguments()).getBook();
        userViewModel.fetchComments(receivedBook.getKey());
        fragmentBookDetailsBinding.bookTitle.setText(receivedBook.getTitle());
        loadCover();
        loadAuthors();
        loadRating();
        loadDescription();
        fragmentBookDetailsBinding.iconAdd.setOnClickListener(v -> {
            NavDirections action = BookDetailsFragmentDirections.actionBookDetailsFragmentToAddToCollectionDialog(receivedBook, user.getIdToken());
            Navigation.findNavController(requireView()).navigate(action);
        });
        loadComments();
        loadAddCommentSection();
    }

    private void initViewModels(){
        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);
    }

    private void loadRating(){
        if(receivedBook.getRating() != null && receivedBook.getRating().getSummary().getAverage() != 0){
            float rating = (float) receivedBook.getRating().getSummary().getAverage();
            fragmentBookDetailsBinding.ratingbarBook.setRating(rating);
            String ratingString = String.format("%s", rating).substring(0,3)
                    .concat(" (")
                    .concat(String.format("%s", receivedBook.getRating().getSummary().getCount()))
                    .concat(")");
            fragmentBookDetailsBinding.textviewRating.setText(ratingString);
        } else {
            fragmentBookDetailsBinding.ratingbarBook.setRating(0);
            fragmentBookDetailsBinding.textviewRating.setText(R.string.rating_not_available);
        }
    }

    private void loadAuthors(){
        StringBuilder authors = new StringBuilder();
        if(receivedBook.getAuthorList() != null){
            for(OLAuthorApiResponse author : receivedBook.getAuthorList()){
                authors.append(author.getName()).append("\n");
            }
        } else {
            authors.append(requireContext().getString(R.string.error_author_not_found));
        }
        fragmentBookDetailsBinding.bookAuthor.setText(authors.toString());
    }

    private void loadDescription() {
        String description = receivedBook.getDescription().getValue();
        for(String option : DESCRIPTION_TRIM_OPTIONS){
            if(description.contains(option)){
                int position = description.indexOf(option);
                description = description.substring(0, position);
            }
        }
        description = description.trim();
        if(description.isEmpty()){
            description = getString(R.string.description_not_available);
        }
        fragmentBookDetailsBinding.bookDescription.setText(description);
    }

    private void loadCover(){
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.loading_spinner)
                .error(R.drawable.image_not_available);

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

    private void loadComments(){

        RecyclerView recyclerviewComments = fragmentBookDetailsBinding.recyclerviewComments;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        user = new User();
        commentAdapter = new CommentAdapter(user, new CommentAdapter.OnItemClickListener() {
            @Override
            public void onCommentClick(Comment comment) {
                if (comment != null && comment.getIdToken() != null) {
                    Log.d("Fragment", "Comment username:" + comment.getIdToken());
                    NavDirections action = BookDetailsFragmentDirections.actionBookDetailsFragmentToUserDetailsFragment(comment.getUser().getIdToken(),comment.getUser().getUsername());
                    Navigation.findNavController(requireView()).navigate(action);
                } else {
                    Log.d("Fragment", "Error - comment is null OR userId is null");
                    // todo handle the error
                }
            }

            @Override
            public void onCommentDelete(Comment comment) {
                userViewModel.deleteComment(receivedBook.getKey(), comment);
                List<Comment> newCommentsList = new ArrayList<>(commentAdapter.getCurrentList());
                newCommentsList.remove(comment);
                commentAdapter.submitList(newCommentsList);
            }
        });

        Log.d("BookDetails Fragment", "Fetch comments start : " + receivedBook.getKey());
        recyclerviewComments.setAdapter(commentAdapter);
        recyclerviewComments.setLayoutManager(layoutManager);
    }

    private void loadAddCommentSection(){
        TextInputLayout textInputLayout = fragmentBookDetailsBinding.edittextComment;
        EditText editText = textInputLayout.getEditText();

        View.OnClickListener commentListener = v -> {
            String commentContent = (editText != null) ? editText.getText().toString() : "";
            commentContent = commentContent.trim();
            if(commentContent.isEmpty()){
                Snackbar.make(requireView(), getString(R.string.snackbar_empty_comment), Snackbar.LENGTH_SHORT).show();
            } else {
                String bookId = receivedBook.getKey();
                String idToken = user.getIdToken();
                userViewModel.addComment(commentContent, bookId, idToken);
                editText.getText().clear();
                InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
                userViewModel.fetchComments(bookId);
            }
        };

        textInputLayout.setEndIconOnClickListener(commentListener);
    }

    private void initObserver(){
        final Observer<List<Result>> commentListObserver = results -> {
            List<Comment> commentResultList = results.stream()
                    .filter(result -> result instanceof Result.CommentSuccess)
                    .map(result -> ((Result.CommentSuccess) result).getData())
                    .collect(Collectors.toList());
            commentAdapter.submitList(commentResultList);
        };
        userViewModel.getCommentList().observe(getViewLifecycleOwner(), commentListObserver);

        final Observer<Result> loggedUserObserver = result -> {
            Log.d("BookDetails fragment", "user changed");
            if(result.isSuccess()) {
                user = ((Result.UserSuccess) result).getData();
                commentAdapter.submitUser(user);
            }
        };
        //get user data from database
        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
    }
}