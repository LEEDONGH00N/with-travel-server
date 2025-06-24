package com.arom.with_travel.domain.chat.service;

import com.arom.with_travel.domain.accompanies.model.Accompany;
import com.arom.with_travel.domain.chat.Chat;
import com.arom.with_travel.domain.chat.ChatPart;
import com.arom.with_travel.domain.chat.Chatroom;
import com.arom.with_travel.domain.chat.dto.ChatRequest;
import com.arom.with_travel.domain.chat.dto.ChatResponse;
import com.arom.with_travel.domain.chat.dto.ChatroomRequest;
import com.arom.with_travel.domain.chat.dto.ChatroomResponse;
import com.arom.with_travel.domain.chat.repository.ChatPartRepository;
import com.arom.with_travel.domain.chat.repository.ChatRepository;
import com.arom.with_travel.domain.chat.repository.ChatroomRedisRepository;
import com.arom.with_travel.domain.chat.repository.ChatroomRepository;
import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.domain.member.repository.MemberRepository;
import com.arom.with_travel.domain.redis.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);
    private final ChatroomRedisRepository chatroomRedisRepository;
    private final ChatroomRepository chatroomRepository;
    private final ChatPartRepository chatPartRepository;
    private final ChatRepository chatRepository;

    private final RedisPublisher redisPublisher;
    //임시
    private final MemberRepository memberRepository;

    //message
    public void sendMessage(ChatRequest.MessageDto messageDto){
        log.info("Received message: {}", messageDto);
        if (Chat.Type.ENTER.equals(messageDto.type())) {
            chatroomRedisRepository.enterChatRoom(messageDto.roomId());
            String msg = messageDto.sender() + "님이 입장하셨습니다.";
            messageDto = messageDto.withMessage(msg);
            log.info("User {} entered room {}", messageDto.sender(), messageDto.roomId());
        }
        else{
            //DB에 메세지 저장하기
//            messageDto.sender()로 멤버 찾기
            Member member = null;
            Chatroom chatroom = chatroomRepository.findChatroomByRoomId(messageDto.roomId());
            chatRepository.save(Chat.builder()
                    .message(messageDto.message())
                    .type(Chat.Type.TALK)
                    .member(member)
                    .chatroom(chatroom)
                    .build());
        }


        System.out.println("messageDto: "+messageDto.message());
        redisPublisher.publish(chatroomRedisRepository.getTopic(messageDto.roomId()), messageDto);
    }

    //Redis사용
    public List<ChatroomResponse.ChatroomDto> findAllRoom(){
        return chatroomRedisRepository.findAllRoom();
    }

    public ChatroomResponse.ChatroomDto createRoom(ChatroomRequest.ChatroomDto chatroomDto){
        String name = chatroomDto.roomName();
        if(name.isEmpty()){
            //이름 비어있을 경우 오류 일으키기
        }
        return chatroomRedisRepository.createChatRoom(name);
    }

    public ChatroomResponse.ChatroomDto roomInfo(ChatroomRequest.ChatroomDto chatroomDto){
        String roomId = chatroomDto.roomId();
        if(roomId.isEmpty()){
            //id널값일 경우
        }
        return chatroomRedisRepository.findRoomById(roomId);
    }


    //db사용
//    public void saveChat(ChatRequest.MessageDto messageDto){
//
//
//
//        Chat chat = Chat.builder()
//                .message(messageDto.message())
//                .type(messageDto.type())
//                .sender()
//                .chatroom()
//                .build();
//
//
//    }

    //db 저장할거
    //채팅방 생성할때, 채팅방 들어갈때
    //메세지 보낼때

    //db조회
    //채팅방 입장 -> 전에 내용불러오기



    public List<ChatroomResponse.ChatroomDto> findRoomByMember(String memberName){
        log.info("enter findRoomByMember membername:{}",memberName);

        //##변경필요
        //        Member member = memberRepository.findMemberByEmail(memberName);
        Member member = null;

        List<ChatPart> chatParts = chatPartRepository.getChatPartsByMember(member);

        if(chatParts.isEmpty()){
            //예외처리
        }

        List<ChatroomResponse.ChatroomDto> chatroomDtos = new ArrayList<>();
        for(ChatPart ct : chatParts){
            chatroomDtos.add(new ChatroomResponse.ChatroomDto(ct.getChatroom().getRoomId(),ct.getChatroom().getRoomName()));
        }

        return chatroomDtos;
    }

    public ChatroomResponse.ChatroomDto saveChatroom(String roomname){

        log.info("enter saveChatroom");

        Member member=null;
        ChatroomResponse.ChatroomDto chatroomDto = createRoom(roomname);

        Chatroom chatroom = Chatroom.builder()
                .roomId(chatroomDto.roomId())
                .roomName(chatroomDto.roomName())
                .build();

        ChatPart chatPart = ChatPart.builder()
                .member(member)
                .chatroom(chatroom)
                .build();

        chatroomRepository.save(chatroom);
        chatPartRepository.save(chatPart);


        log.info("saveChatroom / roomId:{} roomName:{}",chatroom.getRoomId(),chatroom.getRoomName());
        //member에 chatpart 추가
        return chatroomDto;
    }

    public List<ChatResponse.MessageDto> getMessageList(ChatroomRequest.EnterroomDto enterroomDto){

        List<ChatResponse.MessageDto> messageDtos = new ArrayList<>();

        //나가기 전 채탱들만 가져와야함
        Chatroom chatroom = chatroomRepository.findChatroomByRoomId(enterroomDto.roomId());
//        Member member = memberRepository.findMemberByEmail(enterroomDto.memberMail());
        Member member = null;
        List<Chat> chats = chatRepository.findChatsByChatroomAndMember(chatroom,member);

        for(Chat chat : chats){
            messageDtos.add(
                    new ChatResponse.MessageDto(
                            chat.getChatroom().getRoomId(),
                            member.getEmail(),
                            chat.getMessage(),
                            chat.getType()
                    )
            );
        }

        return messageDtos;
    }

    //html용 코드(test)
    public ChatroomResponse.ChatroomDto createRoom(String name){
        return chatroomRedisRepository.createChatRoom(name);
    }

    public ChatroomResponse.ChatroomDto roomInfo(String roomId){
        return chatroomRedisRepository.findRoomById(roomId);
    }



}
