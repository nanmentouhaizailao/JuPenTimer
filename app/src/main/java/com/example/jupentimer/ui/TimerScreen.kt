package com.example.jupentimer.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jupentimer.data.TimerState
import com.example.jupentimer.data.getColorType
import com.example.jupentimer.data.getStateName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    onNavigateToSettings: () -> Unit
) {
    val timerState by viewModel.timerState.collectAsState()
    val totalRounds by viewModel.totalRounds.collectAsState()

    val backgroundColor = when (timerState.getColorType()) {
        "work" -> Color(0xFF4CAF50)
        "rest" -> Color(0xFFFF9800)
        else -> Color(0xFF2196F3)
    }

    val animatedBackgroundColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(500),
        label = "background"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("举盆计时器", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = animatedBackgroundColor
                ),
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "设置",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBackgroundColor)
                .padding(paddingValues)
                .padding(horizontal = 12.dp),
        ) {
            val circleSize = (maxWidth * 0.8f).coerceAtMost(maxHeight * 0.45f)

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 状态显示 - 紧凑
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = timerState.getStateName(),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (timerState is TimerState.Working || timerState is TimerState.Resting) {
                            Text(
                                text = "  ·  第 ${timerState.round} 轮",
                                fontSize = 20.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // 倒计时圆圈 - 自适应
                val displayTime = when (timerState) {
                    is TimerState.Working -> timerState.remainingSeconds
                    is TimerState.Resting -> timerState.remainingSeconds
                    is TimerState.Countdown -> timerState.remainingSeconds
                    is TimerState.Paused -> timerState.remainingSeconds
                    else -> 0
                }

                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(circleSize)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = displayTime.toString().padStart(2, '0'),
                                fontSize = 120.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "秒",
                                fontSize = 28.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

            // 进度信息
            val currentRound = when (timerState) {
                is TimerState.Working -> timerState.round
                is TimerState.Resting -> timerState.round
                is TimerState.Paused -> timerState.round
                else -> 0
            }

            Text(
                text = "总进度: $currentRound / $totalRounds 轮",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 控制按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                when (timerState) {
                    is TimerState.Ready, is TimerState.Finished -> {
                        Button(
                            onClick = { viewModel.startTimer() },
                            modifier = Modifier
                                .width(100.dp)
                                .height(60.dp)
                        ) {
                            Text("开始", fontSize = 18.sp)
                        }
                    }

                    is TimerState.Paused -> {
                        Button(
                            onClick = { viewModel.resumeTimer() },
                            modifier = Modifier
                                .width(100.dp)
                                .height(60.dp)
                        ) {
                            Text("继续", fontSize = 18.sp)
                        }
                        Button(
                            onClick = { viewModel.resetTimer() },
                            modifier = Modifier
                                .width(100.dp)
                                .height(60.dp)
                        ) {
                            Text("重置", fontSize = 18.sp)
                        }
                        Button(
                            onClick = { viewModel.stopTimer() },
                            modifier = Modifier
                                .width(100.dp)
                                .height(60.dp)
                        ) {
                            Text("停止", fontSize = 18.sp)
                        }
                    }

                    is TimerState.Working, is TimerState.Resting, is TimerState.Countdown -> {
                        Button(
                            onClick = { viewModel.pauseTimer() },
                            modifier = Modifier
                                .width(100.dp)
                                .height(60.dp)
                        ) {
                            Text("暂停", fontSize = 18.sp)
                        }
                        Button(
                            onClick = { viewModel.resetTimer() },
                            modifier = Modifier
                                .width(100.dp)
                                .height(60.dp)
                        ) {
                            Text("重置", fontSize = 18.sp)
                        }
                        Button(
                            onClick = { viewModel.stopTimer() },
                            modifier = Modifier
                                .width(100.dp)
                                .height(60.dp)
                        ) {
                            Text("停止", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
        }
    }
}
