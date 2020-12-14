package com.example.financemanager;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.financemanager.FinanceManagerProviderContract.Deposits;

public class DepositFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_DEPOSITS = 0;
    private RecyclerView mRecyclerView;
    private Cursor mDepositCursor;
    private DepositTransactionsRecyclerAdapter mRecyclerAdapter;

    public DepositFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_deposit_transactions);
        mRecyclerAdapter = new DepositTransactionsRecyclerAdapter(getActivity(), null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        LoaderManager.getInstance(this).initLoader(LOADER_DEPOSITS, null, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deposit, container, false);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_DEPOSITS)
            loader = createLoaderDeposits();
        return loader;
    }

    private CursorLoader createLoaderDeposits() {
        String[] columns = {
                Deposits.COLUMN_DEPOSIT_DATE,
                Deposits.COLUMN_DEPOSIT_STATUS,
                Deposits.COLUMN_DEPOSIT_AMOUNT,
                Deposits._ID
        };
        return new CursorLoader(getActivity(), Deposits.CONTENT_URI, columns, null,
                null, Deposits.COLUMN_DEPOSIT_DATE);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_DEPOSITS) {
            mDepositCursor = data;
            mRecyclerAdapter.changeCursor(mDepositCursor);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (loader.getId() == LOADER_DEPOSITS) {
            if (mDepositCursor != null)
                mDepositCursor.close();
        }
    }
}