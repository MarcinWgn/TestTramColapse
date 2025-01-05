package com.example.tramstop

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    val model: MainActivityViewModel by viewModels()

    var timer: Repo.Tmr? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Content()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        timer = model.getTimer()
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
    }

    @Composable
    fun Content() {
        Scaffold(topBar = { MyTopBar() }) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = padding)
            ) { }
            Timetables()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar() {
    TopAppBar(title = {
        Row (verticalAlignment = Alignment.CenterVertically){
            Icon(
                painter = painterResource(R.drawable.tram_24),
                modifier = Modifier.padding(8.dp),
                contentDescription = null
            )
            Text(text = "Tram")
        }

    }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Timetables(model: MainActivityViewModel = viewModel()) {

    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    val baseRz by model.rzebika.observeAsState()
    val baseGr by model.grunwald.observeAsState()


    val pagerState = rememberPagerState() { 2 }

    val pages = listOf("Rzebika", "Rondo Grunwaldzkie")

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            Repo.setState(page)
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TabRow(selectedTabIndex = pagerState.currentPage, divider = {
            HorizontalDivider()
        }) {
            pages.forEachIndexed { index, s ->
                Tab(selected = pagerState.currentPage == index, onClick = {
                    scope.launch {
                        pagerState.scrollToPage(page = index)
                    }
                }
                ) {
                    Text(
                        text = s,
                        color = Color.Black,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) { page ->
            when (page) {
                Repo.RZEBIKA -> {
                    TimeLazyColumn(state = page, base = baseRz)
                }

                Repo.GRUNWALDZKIE -> {
                    TimeLazyColumn(state = page, base = baseGr)
                }
            }
        }
        Progress()
    }
}

fun destFilter(state: Int, actual: ActualItem?): Boolean {
    return if (state == Repo.RZEBIKA) {
        !actual?.direction!!.contains("Mały")
    } else {
        !actual?.direction!!.contains(regex = Regex("Borek|Czerwone"))
    }
}

@Composable
fun TimeLazyColumn(state: Int, base: Response?) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
//            base.let {
//                items(it.actual) { item ->
//                    if (destFilter(state, item)) TramItem(actual = item)
//                }
//            }
            base?.actual?.forEach { a ->
                item {
                    if (destFilter(state, a)) TramItem(actual = a)
                }
            }
        }
    }
}

@Composable
fun TramItem(actual: ActualItem?) {
    val fontSize = 20.sp
    var time = actual?.mixedTime!!.split("%")[0]
    if (!time.contains(":")) time += "min"


    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            Image(
                painter = painterResource(id = R.drawable.tram_24),
                contentDescription = "image",
                alignment = Alignment.Center,
                modifier = Modifier.size(26.dp)
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                fontSize = fontSize,
                text = actual.patternText.toString()
            )
            Text(
                modifier = Modifier.padding(start = 16.dp),
                fontSize = fontSize,
                text = time,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                fontSize = fontSize,
                text = actual.direction ?: "empty",
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
fun Progress(model: MainActivityViewModel = viewModel()) {
    val progress by model.progress.observeAsState()
    LinearProgressIndicator(
        progress = { progress!! },
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
    )
}
