"use strict";(self.webpackChunkberry_free_material_react_cra=self.webpackChunkberry_free_material_react_cra||[]).push([[242],{80242:function(e,t,a){a.r(t);var r=a(72791),s=a(43623),l=a(19035),n=a(61889),i=a(94294),c=a(20890),d=a(2101),o=a(43896),m=a(41985),h=a(97892),x=a.n(h),b=a(35967),u=a(23735),j=a(86930),y=a.n(j),Z=a(80184);x().extend(y()),b.kL.register(b.uw,b.f$,b.ZL,b.Dx,b.u,b.De);t.default=()=>{const[e,t]=(0,r.useState)({}),[a,h]=(0,r.useState)(0),[b,j]=(0,r.useState)(x()().subtract(7,"day")),[y,f]=(0,r.useState)(x()());(0,r.useEffect)((()=>{g("daily",x()().format("YYYY-MM-DD"))}),[]);const g=async(e,a)=>{try{const r=`/estimate/report/${e}/${a}`,s=await(0,l.A_)(r);t(s)}catch(r){console.error("Error fetching estimate data:",r)}};return(0,Z.jsx)(u.Z,{title:"Estimate Report",children:(0,Z.jsxs)(n.ZP,{container:!0,spacing:2,children:[(0,Z.jsx)(n.ZP,{item:!0,lg:5,md:5,sm:5,xs:5,children:(0,Z.jsx)(m.M,{label:"Start Date",value:b,onChange:e=>j(e)})}),(0,Z.jsx)(n.ZP,{item:!0,lg:5,md:5,sm:5,xs:5,children:(0,Z.jsx)(m.M,{label:"End Date",value:y,onChange:e=>f(e)})}),(0,Z.jsx)(n.ZP,{item:!0,lg:2,md:2,sm:2,xs:2,children:(0,Z.jsx)(i.Z,{variant:"contained",color:"primary",onClick:()=>{(async()=>{try{const e=`/estimate/report/daterange?startDate=${b.format("YYYY-MM-DD")}&endDate=${y.format("YYYY-MM-DD")}`,a=await(0,l.A_)(e);t(a)}catch(e){console.error("Error fetching estimate data:",e)}})()},children:"Get Estimate"})}),(0,Z.jsxs)(n.ZP,{item:!0,lg:12,md:12,sm:12,xs:12,children:[(0,Z.jsx)(c.Z,{variant:"h2",children:"Estimate"}),(0,Z.jsxs)(d.Z,{value:a,onChange:(e,t)=>{let a;h(t);const r=x()(),s=r.year(),l=r.month()+1,n=r.format("YYYY-MM-DD"),i=r.isoWeek();switch(t){case 0:a=n;break;case 1:a=`${s}/${i}`;break;case 2:a=`${s}/${l}`;break;case 3:a=`${s}`;break;default:return}g(0===t?"daily":1===t?"weekly":2===t?"monthly":"yearly",a)},children:[(0,Z.jsx)(o.Z,{label:"Daily"}),(0,Z.jsx)(o.Z,{label:"Weekly"}),(0,Z.jsx)(o.Z,{label:"Monthly"}),(0,Z.jsx)(o.Z,{label:"Yearly"})]})]}),(0,Z.jsxs)(n.ZP,{item:!0,xs:12,md:4,children:[(0,Z.jsx)(c.Z,{variant:"h6",children:"Total Estimate Amount"}),(0,Z.jsx)(s.$Q,{data:{labels:["Total Estimate Amount"],datasets:[{label:"Total",data:[e.total||0],backgroundColor:["#3e95cd"]}]}})]}),(0,Z.jsxs)(n.ZP,{item:!0,xs:12,md:4,children:[(0,Z.jsx)(c.Z,{variant:"h6",children:"By Credit Pending Amount"}),(0,Z.jsx)(s.$Q,{data:{labels:["Total Credit Pending Amount"],datasets:[{label:"Credit",data:[e.byCredit||0],backgroundColor:["#8e5ea2"]}]}})]}),(0,Z.jsxs)(n.ZP,{item:!0,xs:12,md:4,children:[(0,Z.jsx)(c.Z,{variant:"h6",children:"Credit/Not Jobs"}),(0,Z.jsx)(s.$Q,{data:(()=>{const t=e.byCredit||{},a=Object.keys(t);return{labels:a,datasets:[{label:"Credit/Not Jobs",data:Object.values(t),backgroundColor:a.map((()=>"#3cba9f"))}]}})()})]})]})})}}}]);
//# sourceMappingURL=242.0b6e74cb.chunk.js.map