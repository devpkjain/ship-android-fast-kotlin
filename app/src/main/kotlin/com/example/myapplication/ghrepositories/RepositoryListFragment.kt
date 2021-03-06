package com.example.myapplication.ghrepositories

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.base.BaseFragment
import com.example.myapplication.databinding.FragmentRepositoryListBinding
import com.example.myapplication.di.ActivityScoped
import com.example.myapplication.util.EndlessRecyclerViewScrollListener
import kotlinx.android.synthetic.main.fragment_repository_list.*
import org.jetbrains.anko.support.v4.ctx
import javax.inject.Inject

@ActivityScoped
class RepositoryListFragment
@Inject constructor() : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: RepositoryListViewModel
    private lateinit var binding: FragmentRepositoryListBinding

    override fun createViewModel() {
        viewModel = ViewModelProviders
            .of(this@RepositoryListFragment, viewModelFactory)
            .get(RepositoryListViewModel::class.java)
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentRepositoryListBinding.inflate(inflater, container, false)
        binding.let {
            it.viewmodel = viewModel
            it.setLifecycleOwner(this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.start()
        viewModel.getRepository().observe(this@RepositoryListFragment, Observer {
            (rvRepository.adapter as RepositoryAdapter).sourceList = it ?: emptyList()
        })

        LinearLayoutManager(ctx).let { ll ->
            rvRepository.adapter = RepositoryAdapter { item ->
                navigateToDetailActivity(item)
            }
            rvRepository.layoutManager = ll
            rvRepository.addOnScrollListener(object : EndlessRecyclerViewScrollListener(ll) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    viewModel.searchNextPage(page)
                }
            })
        }
    }
}
