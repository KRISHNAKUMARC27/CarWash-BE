"use strict";(self.webpackChunkcar_wash_management=self.webpackChunkcar_wash_management||[]).push([[6422],{6422:function(e,a,n){n.r(a);var l=n(72791),s=n(1582),r=n(79836),t=n(56890),o=n(35855),i=n(53994),d=n(53382),c=n(94294),m=n(5574),u=n(65661),y=n(39157),h=n(48550),p=n(68096),x=n(94925),Z=n(58406),v=n(23786),j=n(97123),S=n(97892),f=n.n(S),g=n(41985),A=n(23735),C=n(29464),P=n(19035),b=n(80184);a.default=()=>{const[e,a]=(0,l.useState)([]),[n,S]=(0,l.useState)(f()()),[M,E]=(0,l.useState)(null),[W,w]=(0,l.useState)(!1),[N,k]=(0,l.useState)({salaryPaid:"",paymentMode:"CASH"}),[I,T]=(0,l.useState)(!1),[D,Y]=(0,l.useState)({type:"SALARY-ADVANCE",paymentMode:"",expenseAmount:0,comment:""}),[R,O]=(0,l.useState)(!1),[_,B]=(0,l.useState)(""),[F,U]=(0,l.useState)("");(0,l.useEffect)((()=>{H()}),[]);const H=async()=>{try{const e=await(0,P.A_)("/employee");a(e)}catch(e){console.error("Error fetching employees:",e)}},K=()=>{w(!1),T(!1),E(null),k({salaryPaid:"",paymentMode:""})},L=e=>{const{name:a,value:n}=e.target;k((e=>({...e,[a]:n})))},V=e=>{const{name:a,value:n}=e.target;Y((e=>({...e,[a]:n})))};return(0,b.jsxs)(b.Fragment,{children:[(0,b.jsxs)(A.Z,{title:"Settle Salary",children:[(0,b.jsx)(s.Z,{direction:"row",spacing:2,mb:2,children:(0,b.jsx)(g.M,{label:"Salary Date",value:n,onChange:e=>S(e),format:"YYYY-MM-DD"})}),(0,b.jsxs)(r.Z,{children:[(0,b.jsx)(t.Z,{children:(0,b.jsxs)(o.Z,{children:[(0,b.jsx)(i.Z,{children:"Staff Name"}),(0,b.jsx)(i.Z,{children:"Action"})]})}),(0,b.jsx)(d.Z,{children:e.map((e=>(0,b.jsxs)(o.Z,{children:[(0,b.jsx)(i.Z,{children:e.name}),(0,b.jsxs)(i.Z,{children:[(0,b.jsx)(c.Z,{variant:"contained",color:"success",onClick:()=>(async e=>{try{const a=await(0,P.A_)(`/employee/setupEmployeeSalary/${e}/${n.format("YYYY-MM-DD")}`);E(a),k({salaryPaid:a.salaryEarned-a.salaryAdvance,paymentMode:""}),w(!0)}catch(a){console.error("Error in setupEmployeeSalary:",a),B(a.message||"Error fetching employee salary."),U("error"),O(!0)}})(e.id),children:"Settle Salary"}),(0,b.jsx)(c.Z,{variant:"contained",color:"info",onClick:()=>{Y((a=>({...a,empName:e.name,empId:e.id}))),T(!0)},children:"Pay Advance"})]})]},e.id)))})]})]}),(0,b.jsxs)(m.Z,{open:W,onClose:K,maxWidth:"sm",fullWidth:!0,children:[(0,b.jsxs)(u.Z,{children:["Settle Salary - ",null===M||void 0===M?void 0:M.name]}),(0,b.jsxs)(y.Z,{children:[(0,b.jsx)(h.Z,{fullWidth:!0,margin:"dense",label:"Salary Type",value:(null===M||void 0===M?void 0:M.salaryType)||"",InputProps:{readOnly:!0}}),(0,b.jsx)(h.Z,{fullWidth:!0,margin:"dense",label:"Settlement Type",value:(null===M||void 0===M?void 0:M.salarySettlementType)||"",InputProps:{readOnly:!0}}),(0,b.jsx)(h.Z,{fullWidth:!0,margin:"dense",label:"Salary Earned",value:(null===M||void 0===M?void 0:M.salaryEarned)||"",InputProps:{readOnly:!0}}),(0,b.jsx)(h.Z,{fullWidth:!0,margin:"dense",label:"Salary Advance",value:(null===M||void 0===M?void 0:M.salaryAdvance)||"",InputProps:{readOnly:!0}}),(0,b.jsx)(h.Z,{fullWidth:!0,margin:"dense",label:"Salary To Be Paid",name:"salaryPaid",type:"number",value:N.salaryPaid,onChange:L}),(0,b.jsxs)(p.Z,{fullWidth:!0,margin:"dense",children:[(0,b.jsx)(x.Z,{children:"Payment Mode"}),(0,b.jsxs)(Z.Z,{name:"paymentMode",value:N.paymentMode,onChange:L,label:"Payment Mode",children:[(0,b.jsx)(v.Z,{value:"CASH",children:"Cash"}),(0,b.jsx)(v.Z,{value:"BANK TRANSFER",children:"Bank Transfer"}),(0,b.jsx)(v.Z,{value:"UPI",children:"UPI"})]})]})]}),(0,b.jsxs)(j.Z,{children:[(0,b.jsx)(c.Z,{onClick:K,children:"Cancel"}),(0,b.jsx)(c.Z,{variant:"contained",color:"primary",onClick:async()=>{try{const e={...M,salaryPaid:N.salaryPaid,paymentMode:N.paymentMode,salaryDate:n.toISOString()};await(0,P.j0)("/employee/settleEmployeeSalary",e),B("Salary settled successfully!"),U("success"),O(!0),K()}catch(e){console.error("Error submitting salary:",e),B(e.message||"Failed to submit salary."),U("error"),O(!0)}},children:"Submit"})]})]}),(0,b.jsxs)(m.Z,{open:I,onClose:K,maxWidth:"sm",fullWidth:!0,children:[(0,b.jsxs)(u.Z,{children:["Paying Salary Advance for ",null===D||void 0===D?void 0:D.empName]}),(0,b.jsxs)(y.Z,{children:[(0,b.jsx)(h.Z,{fullWidth:!0,margin:"dense",label:"Expense Type",value:(null===D||void 0===D?void 0:D.type)||"",InputProps:{readOnly:!0}}),(0,b.jsx)(h.Z,{fullWidth:!0,margin:"dense",label:"Advance Amount",name:"expenseAmount",type:"number",value:D.expenseAmount,onChange:V}),(0,b.jsxs)(p.Z,{fullWidth:!0,margin:"dense",children:[(0,b.jsx)(x.Z,{children:"Payment Mode"}),(0,b.jsxs)(Z.Z,{name:"paymentMode",value:D.paymentMode,onChange:V,label:"Payment Mode",children:[(0,b.jsx)(v.Z,{value:"CASH",children:"Cash"}),(0,b.jsx)(v.Z,{value:"BANK TRANSFER",children:"Bank Transfer"}),(0,b.jsx)(v.Z,{value:"UPI",children:"UPI"})]})]}),(0,b.jsx)(h.Z,{fullWidth:!0,margin:"dense",label:"Comment",name:"comment",value:D.comment,onChange:V})]}),(0,b.jsxs)(j.Z,{children:[(0,b.jsx)(c.Z,{onClick:K,children:"Cancel"}),(0,b.jsx)(c.Z,{variant:"contained",color:"primary",onClick:async()=>{try{const e={...D,desc:(null===D||void 0===D?void 0:D.empName)+" - SALARY-ADVANCE"};await(0,P.j0)("/expense/salaryAdvance",e),B("Salary advance added successfully!"),U("success"),O(!0),K()}catch(e){console.error("Error submitting advance:",e),B(e.message||"Failed to submit advance."),U("error"),O(!0)}},children:"Submit"})]})]}),R&&(0,b.jsx)(C.Z,{showAlert:R,setShowAlert:O,alertColor:F,alertMess:_})]})}},65661:function(e,a,n){var l=n(87462),s=n(63366),r=n(72791),t=n(63733),o=n(94419),i=n(20890),d=n(66934),c=n(31402),m=n(17673),u=n(85090),y=n(80184);const h=["className","id"],p=(0,d.ZP)(i.Z,{name:"MuiDialogTitle",slot:"Root",overridesResolver:(e,a)=>a.root})({padding:"16px 24px",flex:"0 0 auto"}),x=r.forwardRef((function(e,a){const n=(0,c.Z)({props:e,name:"MuiDialogTitle"}),{className:i,id:d}=n,x=(0,s.Z)(n,h),Z=n,v=(e=>{const{classes:a}=e;return(0,o.Z)({root:["root"]},m.a,a)})(Z),{titleId:j=d}=r.useContext(u.Z);return(0,y.jsx)(p,(0,l.Z)({component:"h2",className:(0,t.Z)(v.root,i),ownerState:Z,ref:a,variant:"h6",id:null!=d?d:j},x))}));a.Z=x}}]);
//# sourceMappingURL=6422.f61f8d3e.chunk.js.map