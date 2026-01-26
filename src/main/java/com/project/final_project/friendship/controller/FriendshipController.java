package com.project.final_project.friendship.controller;

import static com.project.final_project.common.global.HttpResponseEntity.success;

import com.project.final_project.common.global.HttpResponseEntity.ResponseResult;
import com.project.final_project.friendship.domain.Friendship;
import com.project.final_project.friendship.dto.FriendshipDTO;
import com.project.final_project.friendship.dto.FriendshipRequestDTO;
import com.project.final_project.friendship.dto.FriendshipResponseDTO;
import com.project.final_project.friendship.dto.FriendshipUpdateDTO;
import com.project.final_project.friendship.service.FriendshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friendship")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    @Operation(summary = "모든 친구를 조회함 (검색하고자 하는 유저 기준)", description = "userId에 해당하는 유저의 모든 친구를 조회합니다.")
    @GetMapping("/list")
    public ResponseResult<List<FriendshipResponseDTO>> getAllFriendships(@RequestParam("userId") Integer userId){
        return success(friendshipService.getAllAcceptedFriendships(userId));
    }

    @Operation(summary = "수락되지 않은 친구 요청 목록 조회 (수신자 기준)", description = "수신자가 수락하지 않은 친구 요청 목록을 조회합니다.")
    @GetMapping("/list-receiver-unaccepted/{receiverId}")
    public ResponseResult<List<FriendshipResponseDTO>> getReceiverUnacceptedFriendshipList(@PathVariable("receiverId") Integer receiverId) {
        return success(friendshipService.getUnacceptedFriendshipListByReceiver(receiverId));
    }

    @Operation(summary = "수락되지 않은 친구 요청 목록 조회 (요청자 기준)", description = "요청자가 수락하지 않은 친구 요청 목록을 조회합니다.")
    @GetMapping("/list-requester-unaccepted/{requesterId}")
    public ResponseResult<List<FriendshipResponseDTO>> getRequesterUnacceptedFriendshipList(@PathVariable("requesterId") Integer requesterId) {
        return success(friendshipService.getUnacceptedFriendshipListByRequester(requesterId));
    }

    @Operation(summary = "친구 요청 보내기", description = "특정 사용자에게 친구 요청을 보냅니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "친구 요청을 보냈습니다."),
        @ApiResponse(responseCode = "409", description = "이미 친구 요청이 존재합니다.")
    })
    @PostMapping("/request")
    public ResponseResult<FriendshipDTO> sendFriendRequest(@RequestBody FriendshipRequestDTO dto) {
        return success(friendshipService.sendFriendRequest(dto));
    }

    @Operation(summary = "친구 요청 수락", description = "특정 친구 요청을 수락합니다.")
    @PostMapping("/accept")
    public ResponseEntity<String> acceptFriendRequest(@RequestParam("friendshipId") Integer friendshipId) {
        friendshipService.acceptFriendRequest(friendshipId);
        return ResponseEntity.ok("친구 요청을 수락했습니다.");
    }

    @Operation(summary = "친구 요청 거절", description = "특정 친구 요청을 거절합니다.")
    @PostMapping("/reject")
    public ResponseEntity<String> rejectFriendRequest(@RequestParam("friendshipId") Integer friendshipId) {
        friendshipService.removeFriendship(friendshipId);
        return ResponseEntity.ok("친구 요청을 거절했습니다.");
    }

    @Operation(summary = "친구 요청 취소", description = "보낸 친구 요청을 취소합니다.")
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelFriendRequest(@RequestParam("friendshipId") Integer friendshipId){
        friendshipService.removeFriendship(friendshipId);
        return ResponseEntity.ok("친구 요청을 취소했습니다.");
    }

    @PatchMapping("/message")
    public ResponseResult<FriendshipDTO> updateFriendRequestMessage(@RequestBody FriendshipUpdateDTO dto){
        return success(friendshipService.updateFriendRequestMessage(dto));
    }

    @Operation(summary = "친구 삭제", description = "친구 관계를 삭제합니다.")
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFriendship(
        @RequestParam("friendshipId") Integer friendshipId) {
        friendshipService.removeFriendship(friendshipId);
        return ResponseEntity.ok("친구 삭제 완료");
    }
}
