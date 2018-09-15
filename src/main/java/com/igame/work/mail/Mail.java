package com.igame.work.mail;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igame.core.db.BasicDto;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Transient;

import java.util.Date;

/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(value = "Mail", noClassnameStored = true)
public class Mail    extends BasicDto {
	
	@Indexed
	@JsonIgnore
	public long playerId;//角色ID
	
    private int id;//ID
    
    private int type;//类型  1-奖励邮件，2-普通邮件，3-事件邮件
    
    private int exttype;//类型  1-系统，2-好友
    
    private int state;//状态 0-未读  1-已读 2-已领取
    
    
    private String sender="";//发送者
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date time; //发送时间
    
    private String title="";  //标题
    
    private String content=""; //内容
    
    private String attach=""; //附件

    
    @JsonIgnore
    private long overtime = 0l; //到期时间
  
	@Transient
	@JsonIgnore
	public int dtate;//数据库状态 0-NO 1-新增 2-更新 3-删除

    public Mail() {
    }

    public Mail(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    
    

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getDtate() {
		return dtate;
	}

	@Override
    public boolean equals(Object obj) {
        if (obj instanceof Mail) {
            Mail target = (Mail) obj;
            return target.getId() == this.id;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Mail{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", attach='" + attach + '\'' +
                ", time=" + time +
                '}';
    }

    public long getOvertime() {
        return overtime;
    }

    public void setOvertime(long overtime) {
        this.overtime = overtime;
    }
    
    
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public int getExttype() {
		return exttype;
	}

	public void setExttype(int exttype) {
		this.exttype = exttype;
	}

	public void setDtate(int ndtate) {
		switch (ndtate) {
			case 1://添加
				if (this.dtate == 2 || this.dtate == 3)
					this.dtate = 2;
				else
					this.dtate = 1;
				break;
			case 2://更新
				if(this.dtate == 1){
					this.dtate = 1;
				} else{
					this.dtate = 2;
				}
				break;
			case 3://删除
				if (this.dtate == 1)
					this.dtate = 0;
				else {
					this.dtate = 3;
				}
				break;
			default:
				this.dtate = ndtate;
				break;
		}
	}
}
