package esAction;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.Action;
import esDao.MessageDao;
import esVo.ChatListVo;
import esVo.EmojiVo;

public class OpenMessageAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MessageDao messDao = new MessageDao();
		int userIdx = (Integer)(request.getSession().getAttribute("userIdx"));
		int thisUserIdx = 0;
		try {
			thisUserIdx = Integer.parseInt(request.getParameter("thisUserIdx"));
		} catch(NumberFormatException e) {  }
		boolean checkChatList = messDao.checkChatList(userIdx, thisUserIdx);
		//����ä�ù濡 ���� �� �߰��ϱ�
		if(checkChatList && thisUserIdx > 0) {
			messDao.addChatList(userIdx, thisUserIdx);
			messDao.addChatList(thisUserIdx, userIdx);
		}
		//ä��â �̸�Ƽ�ܸ�� �ޱ�
		ArrayList<EmojiVo> listEmojiVo = messDao.getAllEmoji();
		//ä�ø�� ��������
		ArrayList<ChatListVo> chatList = messDao.getChatList(userIdx);
		
		request.setAttribute("chatList", chatList);
		request.setAttribute("listEmojiVo", listEmojiVo);
		request.getRequestDispatcher("message.jsp").forward(request, response);
	}

}
