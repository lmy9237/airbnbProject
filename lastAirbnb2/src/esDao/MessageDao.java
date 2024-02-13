package esDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import common.DBConnection;
import esVo.ChatDetailVo;
import esVo.ChatListVo;
import esVo.EmojiVo;

public class MessageDao {
	//채팅방에 ?�� ?��모티�?
	static Connection conn = DBConnection.getConnection();
	public ArrayList<EmojiVo> getAllEmoji(){
		ArrayList<EmojiVo> list = new ArrayList<EmojiVo>();
		
		String sql = "SELECT emoji_idx, emoji_url"
				+ " FROM    emoji"
				+ " ORDER BY emoji_idx";
		
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int emojiIdx = rs.getInt("emoji_idx");
				String emojiUrl = rs.getString("emoji_url");
				
				EmojiVo vo = new EmojiVo(emojiIdx, emojiUrl);
				list.add(vo);
			}
			rs.close();
			pstmt.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	//채팅목록 뽑아?���?
	public ArrayList<ChatListVo> getChatList(int userIdx){
		String sql = "SELECT      ui.user_image, ui.user_id, ml.msg_list_idx, ui.user_idx"
				+ " FROM        user_info ui, message_list ml"
				+ " WHERE       ml.partner_idx = ui.user_idx AND ml.host_idx = ?"
				+ " ORDER BY    msg_list_idx DESC";
		ArrayList<ChatListVo> list = new ArrayList<ChatListVo>();
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userIdx);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				String userImage = rs.getString("user_image");
				String userId = rs.getString("user_id");
				int msgListIdx = rs.getInt("msg_list_idx");
				int userIdx1 = rs.getInt("user_idx");

				list.add(new ChatListVo(userImage, userId, msgListIdx, userIdx1));
			}
			rs.close();
			pstmt.close();
		} catch(Exception e) { e.printStackTrace(); }
		
		return list;
	}
	//채팅 목록 �? �??�� 최근 ???�� ?�� �? 뽑아?���?
	public String getRecentChat(int msgListIdx) {
		String sql = "SELECT m.*"
				+ " FROM(SELECT m.msg"
				+ " FROM message_list ml, message m"
				+ " WHERE ml.msg_list_idx = m.msg_list_idx AND ml.msg_list_idx = ?"
				+ " ORDER BY m.msg_idx DESC) m "
				+ " WHERE ROWNUM <= 1";
		String chat = "";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, msgListIdx);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				chat = rs.getString(1);
			}
			rs.close();
			pstmt.close();
		} catch(Exception e) { e.printStackTrace(); }
		
		return chat;
	}
	//ä�ù��� ������ �ִ��� ������ üũ
	public boolean checkChatList(int userIdx, int thisUserIdx) {
		String sql = "SELECT count(*) FROM message_list WHERE host_idx = ? AND partner_idx = ?";
		String check = "0";
		String cnt = "";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(userIdx, thisUserIdx);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				cnt = rs.getString(1);
			}
			rs.close();
			pstmt.close();
		} catch(Exception e) { e.printStackTrace(); }
		
		return check.equals(cnt); 
	}
	//ä�ù� ���� �� �߰��ϱ�
	public void addChatList(int userIdx, int thisUserIdx) {
		String sql = "INSERT INTO message_list(msg_list_idx, partner_idx, host_idx) VALUES (msg_list_idx.nextval, ?, ?)";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userIdx);
			pstmt.setInt(2, thisUserIdx);
			pstmt.executeUpdate();
			pstmt.close();
		} catch(Exception e) { e.printStackTrace(); }
	}
	//ä���� ���� ��������
	public ArrayList<ChatDetailVo> showChatList(int userIdx1, int msgListIdx1){
		String sql ="SELECT ui.user_id, ui.user_image, m.*"
				+ " FROM user_info ui, message m"
				+ " WHERE ui.user_idx = m.user_idx AND m.user_idx = ?  AND msg_list_idx = ?"
				+ " ORDER BY m.msg_idx ASC";
		ArrayList<ChatDetailVo> list = new ArrayList<ChatDetailVo>();
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userIdx1);
			pstmt.setInt(2, msgListIdx1);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				String userId = rs.getString("user_id");
				String userImage = rs.getString("user_image");
				int msgListIdx = rs.getInt("msg_list_idx");
				int userIdx = rs.getInt("user_idx");
				String msg = rs.getString("msg");
				String dateTime = rs.getString("date_time");
				int msgIdx = rs.getInt("msg_idx");
				
				list.add(new ChatDetailVo(userId, userImage, msgListIdx, userIdx, msg, dateTime, msgIdx));
			}
		} catch(Exception e) { e.printStackTrace(); }
		
		return list;
	}
	
}
